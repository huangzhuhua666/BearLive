package com.hzh.bearlive.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hzh.bearlive.api.BaseCallBack;
import com.hzh.bearlive.api.MyOkHttp;
import com.hzh.bearlive.app.MyApplication;
import com.hzh.bearlive.util.Constants;
import com.hzh.bearlive.util.ToastUtils;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveConstants;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 主播直播房间界面
 */
public class HostLiveActivity extends AppCompatActivity {

    @BindView(R.id.live_view)
    AVRootView mLiveView;

    private Timer mHeartBeatTimer = new Timer();

    private int mRoomId;
    private String mUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_live);
        ButterKnife.bind(this);
        ILVLiveManager.getInstance().setAvVideoView(mLiveView);
        createLive();

    }

    /**
     * 创建直播
     */
    private void createLive() {
        mRoomId = getIntent().getIntExtra("roomId", -1);
        mUserId = getIntent().getStringExtra("userId");
        if (mRoomId > 0) {
            ILVLiveConfig liveConfig = MyApplication.getLiveConfig();
            liveConfig.setLiveMsgListener(new ILVLiveConfig.ILVLiveMsgListener() {
                @Override
                public void onNewTextMsg(ILVText text, String SenderId, TIMUserProfile userProfile) {
                    //接收到文本消息
                }

                @Override
                public void onNewCustomMsg(ILVCustomCmd cmd, String id, TIMUserProfile userProfile) {
                    //TODO 接收到自定义消息
                }

                @Override
                public void onNewOtherMsg(TIMMessage message) {
                    //接收到其他消息
                }
            });

            //创建房间配置项
            ILVLiveRoomOption hostOption = new ILVLiveRoomOption(ILiveLoginManager.getInstance().getMyUserId())
                    .controlRole("LiveMaster")//角色设置
                    .autoFocus(true)
                    .autoMic(true)//TODO 修改
                    .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)//权限设置
                    .cameraId(ILiveConstants.FRONT_CAMERA)//TODO 摄像头前置后置
                    .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);//是否开始半自动接收

            //创建房间
            ILVLiveManager.getInstance().createRoom(mRoomId, hostOption, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {//创建成功
                    //开始心形动画
                    //TODO startHeartAnim();
                    //开始发送心跳
                    startHeartBeat();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {//创建失败
                    ToastUtils.showToast("创建直播失败！");
                    finish();
                }
            });
        }

    }

    /**
     * 心跳包检测
     */
    private void startHeartBeat() {
        mHeartBeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //发送心跳包
                MyOkHttp.newBuilder().get().url(Constants.BASE_URL)
                        .addParam("action", Constants.ACTION_HEART_BEAT)
                        .addParam("roomId", mRoomId + "")
                        .addParam("userId", mUserId)
                        .build()
                        .enqueue(new BaseCallBack<String>() {
                            @Override
                            public void onSuccess(String s) {

                            }

                            @Override
                            public void onError(int code) {
                                ToastUtils.showToast("服务器异常！错误码：" + code);
                            }

                            @Override
                            public void onFailure(Call call, IOException e) {
                                ToastUtils.showToast(e.getMessage() + "错误码：-100");
                            }
                        });
            }
        }, 0, 4000); //4秒钟 ，服务器是10秒钟去检测一次。

    }

    /**
     * 主播退出直播房间
     */
    private void quitLive() {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setType(ILVText.ILVTextType.eGroupMsg)
                .setCmd(ILVLiveConstants.ILVLIVE_CMD_LEAVE)
                .setDestId(ILiveRoomManager.getInstance().getIMGroupId());
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveRoomManager.getInstance().quitRoom(new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        finish();
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        finish();
                    }
                });
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });

        //发送退出消息给服务器
        MyOkHttp.newBuilder().get().url(Constants.BASE_URL)
                .addParam("action", Constants.ACTION_QUIT)
                .addParam("roomId", mRoomId + "")
                .addParam("userId", mUserId)
                .build()
                .enqueue(new BaseCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onError(int code) {
                        ToastUtils.showToast("服务器异常！错误码：" + code);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        ToastUtils.showToast(e.getMessage() + "错误码：-100");
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ILVLiveManager.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ILVLiveManager.getInstance().onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHeartBeatTimer.cancel();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        quitLive();

    }

}
