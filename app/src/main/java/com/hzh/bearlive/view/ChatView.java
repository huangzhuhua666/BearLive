package com.hzh.bearlive.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.util.Constants;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVText;

/**
 * 聊天消息输入框
 */
public class ChatView extends LinearLayout {

    private CheckBox mCbSwitch;
    private EditText mEtContent;

    private OnChatSendListener mListener;

    public ChatView(Context context) {
        this(context, null);

    }

    public ChatView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ChatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_chat, this, true);

        mCbSwitch = findViewById(R.id.chat_mode);
        mCbSwitch.setChecked(false);

        mEtContent = findViewById(R.id.et_content);

        TextView btnSend = findViewById(R.id.btn_send);

        mCbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEtContent.setHint("发送弹幕聊天消息");
                } else {
                    mEtContent.setHint("和大家聊点什么吧");
                }
            }
        });

        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChatMsg();
            }
        });

    }

    /**
     * 发送消息
     */
    private void sendChatMsg() {
        if (mListener != null) {
            ILVCustomCmd customCmd = new ILVCustomCmd();
            customCmd.setType(ILVText.ILVTextType.eGroupMsg);
            boolean isDanMu = mCbSwitch.isChecked();
            if (isDanMu) {
                customCmd.setCmd(Constants.CMD_CHAT_MSG_DANMU);
            } else {
                customCmd.setCmd(Constants.CMD_CHAT_MSG_LIST);
            }
            customCmd.setParam(mEtContent.getText().toString());
            mListener.onSend(customCmd);
            mEtContent.setText("");
        }

    }

    /**
     * EditText获取焦点
     */
    public void etGetFocus() {
        mEtContent.setFocusable(true);
        mEtContent.setFocusableInTouchMode(true);
        mEtContent.requestFocus();

    }

    public void setOnChatSendListener(OnChatSendListener listener) {
        mListener = listener;

    }

    public interface OnChatSendListener {

        void onSend(ILVCustomCmd customCmd);

    }

}
