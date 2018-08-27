package com.hzh.bearlive.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.util.ImageUtils;
import com.tencent.TIMUserProfile;

public class PorscheView extends LinearLayout {

    private ImageView mIvAvatar;
    private TextView mTvUserName;

    private AnimationDrawable back;
    private AnimationDrawable front;
    private Animation inAnim;
    private Animation outAnim;
    private OnAvailableListener mListener;

    private int width;
    private int left;

    public PorscheView(Context context) {
        this(context, null);

    }

    public PorscheView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public PorscheView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_porsche, this, true);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvUserName = findViewById(R.id.tv_user_name);
        ImageView wheelBack = findViewById(R.id.wheel_back);
        ImageView wheelFront = findViewById(R.id.wheel_front);

        back = (AnimationDrawable) wheelBack.getDrawable();
        back.setOneShot(false);
        front = (AnimationDrawable) wheelFront.getDrawable();
        front.setOneShot(false);
        setVisibility(GONE);

    }

    public void show(TIMUserProfile userProfile) {
        String avatar = userProfile.getFaceUrl();
        if (TextUtils.isEmpty(avatar)) {
            ImageUtils.loadRound(getContext(), R.drawable.default_avatar, mIvAvatar);
        } else {
            ImageUtils.loadRound(getContext(), "https://" + avatar, mIvAvatar);
        }
        String name = userProfile.getNickName();
        if (TextUtils.isEmpty(name)) {
            name = userProfile.getIdentifier();
        }
        mTvUserName.setText(name);

        post(new Runnable() {
            @Override
            public void run() {
                setVisibility(VISIBLE);
                if (inAnim == null || outAnim == null) {
                    createAnim();
                }
                startAnimation(inAnim);
            }
        });

    }

    private void createAnim() {
        inAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -(width + left) * 1.0f / width,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1,
                Animation.RELATIVE_TO_SELF, 0);
        inAnim.setDuration(2000);
        inAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                back.start();
                front.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                back.stop();
                front.stop();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAnimation(outAnim);
                    }
                }, 2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        outAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, (width + left) * 1.0f / width,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        outAnim.setDuration(2000);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                back.start();
                front.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
                back.stop();
                front.stop();
                if (mListener != null) {
                    mListener.onAvailable();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void setOnAvailableListener(OnAvailableListener listener) {
        mListener = listener;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        width = getWidth();
        left = getLeft();
        createAnim();

    }

    public interface OnAvailableListener {

        void onAvailable();

    }

}
