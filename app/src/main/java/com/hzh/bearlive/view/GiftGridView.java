package com.hzh.bearlive.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.bean.GiftInfo;
import com.hzh.bearlive.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class GiftGridView extends GridView {

    private List<GiftInfo> mGiftList = new ArrayList<>();
    private GridAdapter mAdapter;
    private GiftInfo mSelectedGift;

    private OnGiftClickListener mListener;

    public GiftGridView(Context context) {
        this(context, null);

    }

    public GiftGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public GiftGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        setNumColumns(4);
        mAdapter = new GridAdapter();
        setAdapter(mAdapter);

    }

    public void setGiftList(List<GiftInfo> list) {
        mGiftList.clear();
        mGiftList.addAll(list);
        mAdapter.notifyDataSetChanged();

    }

    public int getGridViewHeight() {
        //获取高度：adapter item 的高度 * 行数
        View item = mAdapter.getView(0, null, this);
        item.measure(0, 0);
        int height = item.getMeasuredHeight();
        return height * 2;

    }

    public void setSelectedGift(GiftInfo gift) {
        mSelectedGift = gift;
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();

    }

    public void setOnGiftClickListener(OnGiftClickListener listener) {
        mListener = listener;

    }

    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mGiftList.size();

        }

        @Override
        public long getItemId(int position) {
            return position;

        }

        @Override
        public Object getItem(int position) {
            return mGiftList.get(position);

        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            final GiftInfo gift = mGiftList.get(position);
            if (convertView == null) {
                view = View.inflate(getContext(), R.layout.view_gift_item, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.bindData(gift);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (gift == GiftInfo.Gift_Empty) {
                        return;
                    }
                    if (mListener != null) {
                        if (gift == mSelectedGift) {
                            mListener.onClick(null);
                        } else {
                            mListener.onClick(gift);
                        }
                    }
                }
            });
            return view;

        }
    }

    private class ViewHolder {

        ImageView iv_gift;
        TextView tv_exp;
        TextView tv_gift_name;
        ImageView iv_select;

        private ViewHolder(View view) {
            iv_gift = view.findViewById(R.id.iv_gift);
            tv_exp = view.findViewById(R.id.tv_exp);
            tv_gift_name = view.findViewById(R.id.tv_gift_name);
            iv_select = view.findViewById(R.id.iv_select);

        }

        private void bindData(GiftInfo gift) {
            ImageUtils.load(getContext(), gift.getGiftResId(), iv_gift);
            if (gift != GiftInfo.Gift_Empty) {
                tv_exp.setText(gift.getExp() + "经验值");
                tv_gift_name.setText(gift.getName());
                if (gift == mSelectedGift) {
                    iv_select.setImageResource(R.drawable.gift_selected);
                } else {
                    if (gift.getType() == GiftInfo.Type.ContinueGift) {
                        iv_select.setImageResource(R.drawable.gift_repeat);
                    } else if (gift.getType() == GiftInfo.Type.FullScreenGift) {
                        iv_select.setImageResource(R.drawable.gift_none);
                    }
                }
            } else {
                tv_exp.setText("");
                tv_gift_name.setText("");
                iv_select.setImageResource(R.drawable.gift_none);
            }

        }

    }

    public interface OnGiftClickListener {

        void onClick(GiftInfo gift);

    }

}
