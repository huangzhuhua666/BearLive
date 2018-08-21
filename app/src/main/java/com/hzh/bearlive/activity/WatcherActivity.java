package com.hzh.bearlive.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hzh.bearlive.api.BaseCallBack;
import com.hzh.bearlive.api.MyOkHttp;
import com.hzh.bearlive.app.MyApplication;
import com.hzh.bearlive.bean.ChatMsgInfo;
import com.hzh.bearlive.bean.ResponseObject;
import com.hzh.bearlive.listener.SoftKeyboardListener;
import com.hzh.bearlive.util.Constants;
import com.hzh.bearlive.util.ToastUtils;
import com.hzh.bearlive.view.BottomControlView;
import com.hzh.bearlive.view.ChatList;
import com.hzh.bearlive.view.ChatView;
import com.hzh.bearlive.view.DanmuView;
import com.hzh.bearlive.view.GiftSelectDialog;
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
    @BindView(R.id.control_view)
    BottomControlView mControlView;
    @BindView(R.id.chat_view)
    ChatView mChatView;
    @BindView(R.id.chat_msg_list)
    ChatList mChatList;
    @BindView(R.id.danmu_view)
    DanmuView mDanmuView;

    private Timer mHeartBeatTimer = new Timer();
    private InputMethodManager imm;
    private GiftSelectDialog mGiftSelectDialog;

    private int mRoomId;
    private String mHostId;
    private String mUserId;

    private GiftSelectDialog.OnGiftSendListener mGiftSendListener = new GiftSelectDialog.OnGiftSendListener() {
        @Override
        public void onSend(ILVCustomCmd customCmd) {
            //TODO
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watcher);
        ButterKnife.bind(this);
        ILVLiveManager.getInstance().setAvVideoView(mLiveView);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        setListener();
        joinRoom();

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

        mControlView.setHost(false);
        mControlView.setOnControlListener(new BottomControlView.OnControlListener() {
            @Override
            public void onChat() {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }

            @Override
            public void onClose() {
                quitRoom();
            }

            @Override
            public void onGift() {
                if (mGiftSelectDialog == null) {
                    mGiftSelectDialog = new GiftSelectDialog(WatcherActivity.this);
                    mGiftSelectDialog.setOnGiftSendListener(mGiftSendListener);
                }
                mGiftSelectDialog.show();
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
                        if (customCmd.getCmd() == Constants.CMD_CHAT_MSG_LIST) {
                            //如果是列表类型的消息，发送给列表显示
                            String content = customCmd.getParam();
                            String id = MyApplication.getSelfProfile().getIdentifier();
                            String avatar = MyApplication.getSelfProfile().getFaceUrl();
                            ChatMsgInfo info = ChatMsgInfo.createListInfo(content, id, avatar);
                            mChatList.addMsgInfo(info);
                        } else if (customCmd.getCmd() == Constants.CMD_CHAT_MSG_DANMU) {
                            String content = customCmd.getParam();
                            String id = MyApplication.getSelfProfile().getIdentifier();
                            String avatar = MyApplication.getSelfProfile().getFaceUrl();
                            String name = MyApplication.getSelfProfile().getNickName();
                            ChatMsgInfo info = ChatMsgInfo.createListInfo(content, id, avatar);
                            mChatList.addMsgInfo(info);
                            //添加到弹幕view
                            if (TextUtils.isEmpty(name)) {
                                name = id;
                            }
                            ChatMsgInfo danmuInfo = ChatMsgInfo.createDanmuInfo(content, id, avatar, name);
                            //添加到弹幕
                            mDanmuView.addDanmu(danmuInfo);
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
                    //接收到自定义消息
                    switch (cmd.getCmd()) {
                        case Constants.CMD_CHAT_MSG_LIST:
                            String content1 = cmd.getParam();
                            ChatMsgInfo info1 = ChatMsgInfo.createListInfo(content1, id, userProfile.getFaceUrl());
                            mChatList.addMsgInfo(info1);
                            break;
                        case Constants.CMD_CHAT_MSG_DANMU:
                            String content2 = cmd.getParam();
                            String name = userProfile.getNickName();
                            ChatMsgInfo info2 = ChatMsgInfo.createListInfo(content2, id, userProfile.getFaceUrl());
                            mChatList.addMsgInfo(info2);
                            if (TextUtils.isEmpty(name)) {
                                name = userProfile.getIdentifier();
                            }
                            ChatMsgInfo danmuInfo = ChatMsgInfo.createDanmuInfo(content2, id,
                                    userProfile.getFaceUrl(), name);
                            //添加到弹幕
                            mDanmuView.addDanmu(danmuInfo);
                            break;
                        case Constants.CMD_CHAT_GIFT:
                            //TODO 界面显示礼物动画
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
