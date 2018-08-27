package com.hzh.bearlive.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.hzh.bearlive.bean.GiftInfo;
import com.tencent.TIMUserProfile;

import java.util.LinkedList;
import java.util.List;

public class GiftFullView extends RelativeLayout {

    private PorscheView mPorscheView;

    private List<GiftUserInfo> mGiftList = new LinkedList<>();

    private boolean isAvailable = false;

    public GiftFullView(Context context) {
        this(context, null);

    }

    public GiftFullView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public GiftFullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        isAvailable = true;

    }

    public void showGift(GiftInfo gift, TIMUserProfile userProfile) {
        synchronized (this) {
            if (gift == null || gift.getType() != GiftInfo.Type.FullScreenGift) {
                return;
            }
            if (isAvailable) {
                isAvailable = false;
                if (gift.getGiftId() == GiftInfo.Gift_BaoShiJie.getGiftId()) {
                    showPorscheView(userProfile);
                }
            } else {
                GiftUserInfo giftUserInfo = new GiftUserInfo(gift, userProfile);
                mGiftList.add(giftUserInfo);
            }
        }

    }

    private void showPorscheView(TIMUserProfile userProfile) {
        synchronized (this) {
            if (mPorscheView == null) {
                mPorscheView = new PorscheView(getContext());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                params.addRule(CENTER_IN_PARENT);
                addView(mPorscheView, params);
                mPorscheView.setOnAvailableListener(new PorscheView.OnAvailableListener() {
                    @Override
                    public void onAvailable() {
                        isAvailable = true;
                        if (mGiftList.size() > 0) {
                            GiftUserInfo giftUserInfo = mGiftList.remove(0);
                            GiftInfo gift = giftUserInfo.gift;
                            TIMUserProfile userProfile1 = giftUserInfo.userProfile;
                            showGift(gift, userProfile1);
                        }
                    }
                });
            }
            mPorscheView.show(userProfile);
        }

    }

    private class GiftUserInfo {

        GiftInfo gift;
        TIMUserProfile userProfile;

        private GiftUserInfo(GiftInfo gift, TIMUserProfile userProfile) {
            this.gift = gift;
            this.userProfile = userProfile;

        }

    }

}
