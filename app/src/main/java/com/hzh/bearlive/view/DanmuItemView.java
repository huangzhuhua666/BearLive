package com.hzh.bearlive.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.bean.ChatMsgInfo;
import com.hzh.bearlive.util.ImageUtils;

/**
 * 弹幕
 */
public class DanmuItemView extends RelativeLayout {

    private ImageView mIvAvatar;
    private TextView mTvName;
    private TextView mTvContent;

    private TranslateAnimation mAnimation;
    private OnAvailableListener mListener;

    public DanmuItemView(Context context) {
        this(context, null);

    }

    public DanmuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public DanmuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_danmu_item, this, true);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvName = findViewById(R.id.tv_name);
        mTvContent = findViewById(R.id.tv_content);

        //创建动画，水平位移
        mAnimation = (TranslateAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.danmu_item_enter);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
                if (mListener != null) {
                    mListener.onAvailable();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    /**
     * 显示弹幕
     *
     * @param danmuInfo info
     */
    public void showDanmu(ChatMsgInfo danmuInfo) {
        String avatar = danmuInfo.getAvatar();
        if (TextUtils.isEmpty(avatar)) {
            ImageUtils.loadRound(getContext(), R.drawable.default_avatar, mIvAvatar);
        } else {
            ImageUtils.loadRound(getContext(), "https://" + avatar, mIvAvatar);
        }
        mTvName.setText(danmuInfo.getName());
        mTvContent.setText(danmuInfo.getContent());

        //在动画监听里面做处理，调用post保证在动画结束之后再start
        //解决start之后直接end的情况
        post(new Runnable() {
            @Override
            public void run() {
                setVisibility(VISIBLE);
                startAnimation(mAnimation);
            }
        });

    }

    public void setOnAvailableListener(OnAvailableListener listener) {
        mListener = listener;

    }

    public interface OnAvailableListener {

        void onAvailable();

    }

}
