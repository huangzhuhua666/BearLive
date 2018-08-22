package com.hzh.bearlive.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.hzh.bearlive.api.BaseCallBack;
import com.hzh.bearlive.api.MyOkHttp;
import com.hzh.bearlive.app.MyApplication;
import com.hzh.bearlive.bean.ChatMsgInfo;
import com.hzh.bearlive.bean.GiftCmdInfo;
import com.hzh.bearlive.bean.GiftInfo;
import com.hzh.bearlive.listener.SoftKeyboardListener;
import com.hzh.bearlive.util.Constants;
import com.hzh.bearlive.util.ToastUtils;
import com.hzh.bearlive.view.BottomControlView;
import com.hzh.bearlive.view.ChatList;
import com.hzh.bearlive.view.ChatView;
import com.hzh.bearlive.view.DanmuView;
import com.hzh.bearlive.view.GiftRepeatView;
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
    @BindView(R.id.control_view)
    BottomControlView mControlView;
    @BindView(R.id.chat_view)
    ChatView mChatView;
    @BindView(R.id.chat_msg_list)
    ChatList mChatList;
    @BindView(R.id.danmu_view)
    DanmuView mDanmuView;
    @BindView(R.id.gift_repeat_view)
    GiftRepeatView mGiftRepeatView;

    private Timer mHeartBeatTimer = new Timer();
    private InputMethodManager imm;

    private int mRoomId;
    private String mUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_live);
        ButterKnife.bind(this);
        ILVLiveManager.getInstance().setAvVideoView(mLiveView);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        setListener();
        createLive();

    }

    private void setListener() {
        //软键盘显示、隐藏状态的监听
        SoftKeyboardListener.setListener(this, new SoftKeyboardListener.OnSoftKeyboardChangeListener() {
            @Override
            public void onShow() {
                mControlView.setVisibility(View.GONE);
                mChatView.setVisibility(View.VISIBLE);
                mChatView.etGetFocus();
            }

            @Override
            public void onHide() {
                mControlView.setVisibility(View.VISIBLE);
                mChatView.setVisibility(View.GONE);
            }
        });

        mControlView.setHost(true);
        mControlView.setOnControlListener(new BottomControlView.OnControlListener() {
            @Override
            public void onChat() {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }

            @Override
            public void onClose() {
                quitLive();
            }

            @Override
            public void onGift() {
                //礼物
            }
        });

        mChatView.setOnChatSendListener(new ChatView.OnChatSendListener() {
            @Override
            public void onSend(final ILVCustomCmd customCmd) {
                //发送消息
                customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
                ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack<TIMMessage>() {
                    @Override
                    public void onSuccess(TIMMessage data) {
                        String content = customCmd.getParam();
                        String id = MyApplication.getSelfProfile().getIdentifier();
                        String avatar = MyApplication.getSelfProfile().getFaceUrl();
                        String name = MyApplication.getSelfProfile().getNickName();
                        if (TextUtils.isEmpty(name)) {
                            name = id;
                        }
                        ChatMsgInfo info = ChatMsgInfo.createListInfo(content, id, avatar);
                        switch (customCmd.getCmd()) {
                            case Constants.CMD_CHAT_MSG_LIST:
                                //如果是列表类型的消息，发送给列表显示
                                mChatList.addMsgInfo(info);
                                break;
                            case Constants.CMD_CHAT_MSG_DANMU:
                                mChatList.addMsgInfo(info);
                                //添加到弹幕view
                                ChatMsgInfo danmuInfo = ChatMsgInfo.createDanmuInfo(content, id, avatar, name);
                                //添加到弹幕
                                mDanmuView.addDanmu(danmuInfo);
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {

                    }
                });
            }
        });

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
                    // 接收到自定义消息
                    String content = cmd.getParam();
                    String name = userProfile.getNickName();
                    if (TextUtils.isEmpty(name)) {
                        name = userProfile.getIdentifier();
                    }
                    ChatMsgInfo info = ChatMsgInfo.createListInfo(content, id, userProfile.getFaceUrl());
                    switch (cmd.getCmd()) {
                        case Constants.CMD_CHAT_MSG_LIST:
                            mChatList.addMsgInfo(info);
                            break;
                        case Constants.CMD_CHAT_MSG_DANMU:
                            mChatList.addMsgInfo(info);
                            ChatMsgInfo danmuInfo = ChatMsgInfo.createDanmuInfo(content, id,
                                    userProfile.getFaceUrl(), name);
                            //添加到弹幕
                            mDanmuView.addDanmu(danmuInfo);
                            break;
                        case Constants.CMD_CHAT_GIFT:
                            //界面显示礼物动画
                            GiftCmdInfo giftCmdInfo = new Gson().fromJson(cmd.getParam(), GiftCmdInfo.class);
                            GiftInfo gift = GiftInfo.getGiftById(giftCmdInfo.getGiftId());
                            String repeatId = giftCmdInfo.getRepeatId();
                            if (gift == null) {
                                return;
                            }
                            switch (gift.getType()) {
                                case ContinueGift:
                                    mGiftRepeatView.showGift(gift, repeatId, userProfile);
                                    break;
                                case FullScreenGift:
                                    //全屏礼物
                                    //TODO
                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
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
            ILVLiveManager.getInstance().

                    createRoom(mRoomId, hostOption, new ILiveCallBack() {
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
