package com.hzh.bearlive.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.hzh.bearlive.activity.R;

/**
 * 底部控制栏
 */
public class BottomControlView extends RelativeLayout implements View.OnClickListener {

    private OnControlListener mListener;

    public BottomControlView(Context context) {
        this(context, null);

    }

    public BottomControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BottomControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_bottom_control, this, true);
        findViewById(R.id.btn_chat).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);

    }

    public void setOnControlListener(OnControlListener listener) {
        mListener = listener;

    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.btn_chat:
                    // 聊天
                    mListener.onChat();
                    break;
                case R.id.btn_close:
                    // 关闭
                    mListener.onClose();
                    break;
                default:
                    break;
            }
        }

    }

    public interface OnControlListener {

        void onChat();

        void onClose();

    }

}
