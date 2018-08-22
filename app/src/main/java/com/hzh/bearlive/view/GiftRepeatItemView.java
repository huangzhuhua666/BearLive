package com.hzh.bearlive.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.bean.GiftInfo;
import com.hzh.bearlive.util.ImageUtils;
import com.tencent.TIMUserProfile;

public class GiftRepeatItemView extends RelativeLayout {

    private ImageView mIvAvatar;
    private TextView mTvUserName;
    private TextView mTvGiftName;
    private ImageView mIvGift;
    private TextView mTvGiftNum;

    private Animation mViewInAnim;
    private Animation mViewOutAnim;
    private Animation mTextScaleAnim;
    private OnAvailableListener mListener;

    private int giftId = -1;
    private String userId = "";
    private String repeatId = "";
    private int leftNum = 0;
    private int totalNum = 0;

    public GiftRepeatItemView(Context context) {
        this(context, null);

    }

    public GiftRepeatItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public GiftRepeatItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAnim();

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_gift_repeat_item, this, true);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvUserName = findViewById(R.id.tv_user_name);
        mTvGiftName = findViewById(R.id.tv_gift_name);
        mIvGift = findViewById(R.id.iv_gift);
        mTvGiftNum = findViewById(R.id.tv_gift_num);

    }

    private void initAnim() {
        mViewInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.repeat_gift_in);
        mViewInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTvGiftNum.setVisibility(VISIBLE);
                post(new Runnable() {
                    @Override
                    public void run() {
                        mTvGiftNum.startAnimation(mTextScaleAnim);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mTextScaleAnim = AnimationUtils.loadAnimation(getContext(), R.anim.repeat_gift_num_scale);
        mTextScaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mTvGiftNum.setText("x" + totalNum);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (leftNum > 0) {
                    leftNum--;
                    totalNum++;
                    post(new Runnable() {
                        @Override
                        public void run() {
                            //在view显示完成之后，再进行img的动画
                            mTvGiftNum.startAnimation(mTextScaleAnim);
                        }
                    });
                } else {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            startAnimation(mViewOutAnim);
                        }
                    });
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mViewOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.repeat_gift_out);
        mViewOutAnim.setAnimationListener(new Animation.AnimationListener() {
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

    public void showGift(GiftInfo gift, String repeatId, TIMUserProfile senderProfile) {
        giftId = gift.getGiftId();
        userId = senderProfile.getIdentifier();
        this.repeatId = repeatId;

        if (getVisibility() != VISIBLE) {
            totalNum = 1;
            //所有动画结束之后
            String avatar = senderProfile.getFaceUrl();
            if (TextUtils.isEmpty(avatar)) {
                ImageUtils.load(getContext(), R.drawable.default_avatar, mIvAvatar);
            } else {
                ImageUtils.load(getContext(), "https://" + avatar, mIvAvatar);
            }

            String userName = senderProfile.getNickName();
            if (TextUtils.isEmpty(userName)) {
                userName = senderProfile.getIdentifier();
            }
            mTvUserName.setText(userName);

            mTvGiftName.setText("送出了" + gift.getName());

            ImageUtils.load(getContext(), gift.getGiftResId(), mIvGift);

            mTvGiftNum.setText("x" + 1);

            //开启动画
            post(new Runnable() {
                @Override
                public void run() {
                    setVisibility(VISIBLE);
                    mTvGiftNum.setVisibility(GONE);
                    startAnimation(mViewInAnim);
                }
            });
        } else {
            //需要记录下还需要显示多少次礼物
            leftNum++;
        }

    }

    public void setOnAvailableListener(OnAvailableListener listener) {
        mListener = listener;

    }

    public boolean isAvailable(GiftInfo gift, String repeatId, TIMUserProfile senderProfile) {
        boolean sameGift = giftId == gift.getGiftId();
        boolean sameRepeat = this.repeatId.equals(repeatId);
        boolean sameSender = userId.equals(senderProfile.getIdentifier());
        boolean isContinue = gift.getType() == GiftInfo.Type.ContinueGift;
        return sameGift && sameRepeat && sameSender && isContinue;

    }

    public interface OnAvailableListener {

        void onAvailable();

    }

}
