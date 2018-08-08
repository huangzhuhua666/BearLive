package com.hzh.bearlive.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.hzh.bearlive.api.BaseCallBack;
import com.hzh.bearlive.api.MyOkHttp;
import com.hzh.bearlive.app.MyApplication;
import com.hzh.bearlive.bean.ResponseObject;
import com.hzh.bearlive.util.Constants;
import com.hzh.bearlive.util.ToastUtils;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveConstants;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class WatcherActivity extends AppCompatActivity {

    @BindView(R.id.live_view)
    AVRootView mLiveView;

    private Timer mHeartBeatTimer = new Timer();

    private int mRoomId;
    private String mHostId;
    private String mUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watcher);
        ButterKnife.bind(this);
        ILVLiveManager.getInstance().setAvVideoView(mLiveView);
        joinRoom();

    }

    /**
     * 加入房间
     */
    private void joinRoom() {
        mRoomId = getIntent().getIntExtra("roomId", -1);
        mHostId = getIntent().getStringExtra("hostId");
        mUserId = MyApplication.getSelfProfile().getIdentifier();
        if (mRoomId > 0 && !TextUtils.isEmpty(mHostId)) {
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

            //加入房间配置项
            ILVLiveRoomOption memberOption = new ILVLiveRoomOption(mHostId)
                    .controlRole("Guest")//角色设置
                    .autoCamera(false)//是否自动打开摄像头
                    .autoMic(false)//是否自动打开mic
                    .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM |
                            AVRoomMulti.AUTH_BITS_RECV_AUDIO |
                            AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO |
                            AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO)//权限设置
                    .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);//是否开始半自动接收

            //加入房间
            ILVLiveManager.getInstance().joinRoom(mRoomId, memberOption, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    //通知服务器加入房间
                    notifyServiceJoin();
                    //开始心形动画
                    //TODO startHeartAnim();
                    //同时发送进入直播的消息。
                    sendEnterRoomMsg();
                    //显示主播的头像
                    updateTitleView();
                    //开始心跳包
                    startHeartBeat();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    ToastUtils.showToast("直播已结束！");
                    quitRoom();
                }
            });
        }

    }

    /**
     * 通知服务器加入房间
     */
    private void notifyServiceJoin() {
        MyOkHttp.newBuilder().get().url(Constants.BASE_URL)
                .addParam("action", Constants.ACTION_JOIN)
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

    /**
     * 发送进入直播的消息
     */
    private void sendEnterRoomMsg() {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setType(ILVText.ILVTextType.eGroupMsg)
                .setCmd(ILVLiveConstants.ILVLIVE_CMD_ENTER)
                .setDestId(ILiveRoomManager.getInstance().getIMGroupId());
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });

    }

    /**
     * 显示主播的头像
     */
    private void updateTitleView() {
        List<String> list = new ArrayList<>();
        list.add(mHostId);
        TIMFriendshipManager.getInstance().getUsersProfile(list, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {//失败
                //TODO titleView.setHost(null);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) { //只有一个主播的信息
                //TODO titleView.setHost(timUserProfiles.get(0));
            }
        });
        // 添加自己的头像到titleView上。
        //TODO titleView.addWatcher(MyApplication().getSelfProfile());

        //请求已经加入房间的成员信息
        MyOkHttp.newBuilder().get().url(Constants.BASE_URL)
                .addParam("action", Constants.ACTION_GET_WATCHER)
                .addParam("roomId", mRoomId + "")
                .build()
                .enqueue(new BaseCallBack<ResponseObject<Set<String>>>() {
                    @Override
                    public void onSuccess(ResponseObject<Set<String>> responseObject) {
                        if (responseObject.getCode().equals(ResponseObject.CODE_FAIL)) {
                            ToastUtils.showToast(responseObject.getErrMsg() + "错误码：" +
                                    responseObject.getErrCode());
                        } else if (responseObject.getCode().equals(ResponseObject.CODE_SUCCESS)) {
                            Set<String> watchers = responseObject.getData();
                            if (watchers != null) {
                                List<String> watcherList = new ArrayList<>(watchers);
                                TIMFriendshipManager.getInstance().getUsersProfile(watcherList,
                                        new TIMValueCallBack<List<TIMUserProfile>>() {
                                            @Override
                                            public void onError(int i, String s) {//失败

                                            }

                                            @Override
                                            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                                                //添加已经在房间的观众信息
                                                //TODO titleView.addWatchers(timUserProfiles);
                                            }
                                        });
                            }
                        }
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
        }, 0, 4000);//4秒钟 ，服务器是10秒钟去检测一次。

    }

    private void quitRoom() {
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
        quitRoom();

    }
}
