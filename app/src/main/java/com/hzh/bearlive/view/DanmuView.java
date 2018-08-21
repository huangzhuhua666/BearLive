package com.hzh.bearlive.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.bean.ChatMsgInfo;

import java.util.LinkedList;
import java.util.List;

public class DanmuView extends LinearLayout {

    private DanmuItemView item1, item2, item3, item4;
    private List<ChatMsgInfo> mDanmuList = new LinkedList<>();

    private DanmuItemView.OnAvailableListener mListener = new DanmuItemView.OnAvailableListener() {
        @Override
        public void onAvailable() {
            synchronized (this) {
                //有可用的itemview
                //从msgList中获取之前缓存下来的消息，然后显示出来
                if (mDanmuList.size() > 0) {
                    ChatMsgInfo danmuInfo = mDanmuList.remove(0);
                    if (danmuInfo != null) {
                        addDanmu(danmuInfo);
                    }
                }
            }
        }
    };

    public DanmuView(Context context) {
        this(context, null);

    }

    public DanmuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public DanmuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_danmu, this, true);
        item1 = findViewById(R.id.danmu1);
        item1.setVisibility(GONE);
        item1.setOnAvailableListener(mListener);

        item2 = findViewById(R.id.danmu2);
        item2.setVisibility(GONE);
        item2.setOnAvailableListener(mListener);

        item3 = findViewById(R.id.danmu3);
        item3.setVisibility(GONE);
        item3.setOnAvailableListener(mListener);

        item4 = findViewById(R.id.danmu4);
        item4.setVisibility(GONE);
        item4.setOnAvailableListener(mListener);

    }

    public void addDanmu(ChatMsgInfo danmuInfo) {
        synchronized (this) {
            DanmuItemView available = getAvailableItem();
            if (available == null) {
                //说明没有可用的itemView
                mDanmuList.add(danmuInfo);
            } else {
                //说明有可用的itemView
                available.showDanmu(danmuInfo);
            }
        }

    }

    /**
     * 获取可用的item
     *
     * @return 可用的item
     */
    private DanmuItemView getAvailableItem() {
        if (item1.getVisibility() != VISIBLE) {
            return item1;
        }
        if (item2.getVisibility() != VISIBLE) {
            return item2;
        }
        if (item3.getVisibility() != VISIBLE) {
            return item3;
        }
        if (item4.getVisibility() != VISIBLE) {
            return item4;
        }
        return null;

    }

}
