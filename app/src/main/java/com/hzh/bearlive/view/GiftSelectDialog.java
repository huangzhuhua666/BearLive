package com.hzh.bearlive.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.bean.GiftCmdInfo;
import com.hzh.bearlive.bean.GiftInfo;
import com.hzh.bearlive.util.Constants;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVText;

import java.util.ArrayList;
import java.util.List;

public class GiftSelectDialog extends TransParentDialog {

    private ViewPager mVpGift;
    private ImageView mIvIndicator1;
    private ImageView mIvIndicator2;
    private Button mBtnSend;

    private static List<GiftInfo> mGiftList = new ArrayList<>();
    private List<GiftGridView> pages = new ArrayList<>();
    private GiftInfo mSelectedGift;
    private OnGiftSendListener mListener;
    private Handler sendRepeatTimer = new Handler();

    private Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            mBtnSend.setText("发送(" + leftTime + "s)");
            sendRepeatTimer.postDelayed(minutesTime, 200);
        }
    };

    private Runnable minutesTime = new Runnable() {
        @Override
        public void run() {
            leftTime--;
            if (leftTime > 0) {
                mBtnSend.setText("发送(" + leftTime + "s");
                sendRepeatTimer.postDelayed(this, 200);
            } else {
                mBtnSend.setText("发送");
                repeatId = "";
            }
        }
    };

    private int leftTime = 10;
    private String repeatId = "";

    static {
        mGiftList.add(GiftInfo.Gift_BingGun);
        mGiftList.add(GiftInfo.Gift_BingJiLing);
        mGiftList.add(GiftInfo.Gift_MeiGui);
        mGiftList.add(GiftInfo.Gift_PiJiu);
        mGiftList.add(GiftInfo.Gift_HongJiu);
        mGiftList.add(GiftInfo.Gift_Hongbao);
        mGiftList.add(GiftInfo.Gift_ZuanShi);
        mGiftList.add(GiftInfo.Gift_BaoXiang);
        mGiftList.add(GiftInfo.Gift_BaoShiJie);
    }

    public GiftSelectDialog(Activity activity) {
        super(activity, true);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_gift_select, null,
                false);
        setContentView(view);
        setWidthAndHeight(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        mVpGift = view.findViewById(R.id.vp_gift);
        mVpGift.setAdapter(new GiftAdapter(activity));

        mIvIndicator1 = view.findViewById(R.id.iv_indicator1);
        mIvIndicator2 = view.findViewById(R.id.iv_indicator2);

        mBtnSend = view.findViewById(R.id.btn_send);
        mBtnSend.setEnabled(false);
        setOnClick();

    }

    public void setOnGiftSendListener(OnGiftSendListener listener) {
        mListener = listener;

    }

    private void restartTimer() {
        stopTimer();
        sendRepeatTimer.post(updateTime);

    }

    private void stopTimer() {
        sendRepeatTimer.removeCallbacks(updateTime);
        sendRepeatTimer.removeCallbacks(minutesTime);
        mBtnSend.setText("发送");
        leftTime = 10;

    }

    @Override
    protected void setOnClick() {
        mVpGift.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mIvIndicator1.setImageResource(R.drawable.ind_s);
                    mIvIndicator2.setImageResource(R.drawable.ind_uns);
                } else if (position == 1) {
                    mIvIndicator1.setImageResource(R.drawable.ind_uns);
                    mIvIndicator2.setImageResource(R.drawable.ind_s);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    if (TextUtils.isEmpty(repeatId)) {
                        repeatId = System.currentTimeMillis() + "";
                    }
                    ILVCustomCmd customCmd = new ILVCustomCmd();
                    customCmd.setCmd(Constants.CMD_CHAT_GIFT);
                    customCmd.setType(ILVText.ILVTextType.eGroupMsg);
                    GiftCmdInfo giftCmdInfo = new GiftCmdInfo(mSelectedGift.getGiftId(), repeatId);
                    customCmd.setParam(new Gson().toJson(giftCmdInfo));
                    mListener.onSend(customCmd);
                    if (mSelectedGift.getType() == GiftInfo.Type.ContinueGift) {
                        restartTimer();
                    }
                }
            }
        });

    }

    public void show() {
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (params != null) {
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
        }
        super.show();

    }

    public interface OnGiftSendListener {

        void onSend(ILVCustomCmd customCmd);

    }

    private class GiftAdapter extends PagerAdapter {

        private Context mContext;

        private GiftAdapter(Context context) {
            mContext = context;

        }

        @Override
        public int getCount() {
            return 2;

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;

        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            //创建item view
            GiftGridView itemView = new GiftGridView(mContext);
            itemView.setOnGiftClickListener(new GiftGridView.OnGiftClickListener() {
                @Override
                public void onClick(GiftInfo gift) {
                    mSelectedGift = gift;
                    if (mSelectedGift != null) {
                        mBtnSend.setEnabled(true);
                    } else {
                        mBtnSend.setEnabled(false);
                    }

                    for (GiftGridView page : pages) {
                        page.setSelectedGift(mSelectedGift);
                        page.notifyDataSetChanged();
                    }
                }
            });

            //确定当前页面所展示的gift的list
            int startIndex = position * 8;
            int endIndex = (position + 1) * 8;
            int emptyNum = 0;
            //最后一页的边界处理
            if (endIndex > mGiftList.size()) {
                emptyNum = endIndex - mGiftList.size();
                endIndex = mGiftList.size();
            }

            List<GiftInfo> targetList = mGiftList.subList(startIndex, endIndex);
            //超出边界的，用空填充。保证每个页面都有item
            for (int i = 0; i < emptyNum; i++) {
                targetList.add(GiftInfo.Gift_Empty);
            }
            itemView.setGiftList(targetList);

            ViewGroup.LayoutParams params = container.getLayoutParams();
            params.height = itemView.getGridViewHeight();
            container.setLayoutParams(params);
            container.addView(itemView);
            pages.add(itemView);
            return itemView;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
            pages.remove(object);

        }
    }

}
