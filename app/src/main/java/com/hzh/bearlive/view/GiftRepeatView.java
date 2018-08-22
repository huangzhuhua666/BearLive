package com.hzh.bearlive.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.bean.GiftInfo;
import com.tencent.TIMUserProfile;

import java.util.LinkedList;
import java.util.List;

public class GiftRepeatView extends LinearLayout {

    private GiftRepeatItemView item1, item2;

    private List<GiftSenderInfo> mSendList = new LinkedList<>();

    private GiftRepeatItemView.OnAvailableListener mListener = new GiftRepeatItemView.OnAvailableListener() {
        @Override
        public void onAvailable() {
            synchronized (this) {
                //有可用的itemview
                //获取之前缓存下来的消息，然后显示出来
                if (mSendList.size() > 0) {
                    GiftSenderInfo send = mSendList.remove(0);
                    if (send != null) {
                        showGift(send.gift, send.repeatId, send.senderProfile);
                    }
                }
            }
        }
    };

    public GiftRepeatView(Context context) {
        this(context, null);

    }

    public GiftRepeatView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public GiftRepeatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_gift_repeat, this, true);
        item1 = findViewById(R.id.gift1);
        item1.setVisibility(GONE);
        item1.setOnAvailableListener(mListener);

        item2 = findViewById(R.id.gift2);
        item2.setVisibility(GONE);
        item2.setOnAvailableListener(mListener);

    }

    public void showGift(GiftInfo gift, String repeatId, TIMUserProfile senderProfile) {
        synchronized (this) {
            GiftRepeatItemView available = getAvailableItem(gift, repeatId, senderProfile);
            if (available == null) {
                GiftSenderInfo send = new GiftSenderInfo(gift, repeatId, senderProfile);
                mSendList.add(send);
            } else {
                available.showGift(gift, repeatId, senderProfile);
            }
        }

    }

    private GiftRepeatItemView getAvailableItem(GiftInfo gift, String repeatId, TIMUserProfile senderProfile) {
        if (item1.isAvailable(gift, repeatId, senderProfile)) {
            return item1;
        }
        if (item2.isAvailable(gift, repeatId, senderProfile)) {
            return item2;
        }
        if (item1.getVisibility() != VISIBLE) {
            return item1;
        }
        if (item2.getVisibility() != VISIBLE) {
            return item2;
        }
        return null;

    }

    private class GiftSenderInfo {

        private GiftInfo gift;
        private String repeatId;
        private TIMUserProfile senderProfile;

        private GiftSenderInfo(GiftInfo gift, String repeatId, TIMUserProfile senderProfile) {
            this.gift = gift;
            this.repeatId = repeatId;
            this.senderProfile = senderProfile;

        }

    }

}
