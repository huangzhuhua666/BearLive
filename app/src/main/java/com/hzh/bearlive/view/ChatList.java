package com.hzh.bearlive.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.bean.ChatMsgInfo;
import com.hzh.bearlive.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示聊天信息的ListView
 */
public class ChatList extends RelativeLayout {

    private ChatMsgAdapter mAdapter;
    private ListView mLvChat;

    public ChatList(Context context) {
        this(context, null);

    }

    public ChatList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ChatList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_chat_list, this, true);
        mLvChat = findViewById(R.id.chat_list);
        mAdapter = new ChatMsgAdapter();
        mLvChat.setAdapter(mAdapter);
        mLvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
            }
        });

    }

    /**
     * 添加聊天信息到列表
     *
     * @param info info
     */
    public void addMsgInfo(ChatMsgInfo info) {
        if (info != null) {
            mAdapter.addMsgInfo(info);
            mLvChat.smoothScrollToPosition(mAdapter.getCount());
        }

    }

    /**
     * 添加聊天消息到列表
     *
     * @param infos infos
     */
    public void addMsgInfos(List<ChatMsgInfo> infos) {
        if (infos != null) {
            mAdapter.addMsgInfos(infos);
            mLvChat.smoothScrollToPosition(mAdapter.getCount());
        }

    }

    private class ChatMsgAdapter extends BaseAdapter {

        private List<ChatMsgInfo> mChatInfos = new ArrayList<>();

        private void addMsgInfo(ChatMsgInfo info) {
            if (info != null) {
                mChatInfos.add(info);
                notifyDataSetChanged();
            }

        }

        private void addMsgInfos(List<ChatMsgInfo> infos) {
            if (infos != null) {
                mChatInfos.addAll(infos);
                notifyDataSetChanged();
            }

        }

        @Override
        public int getCount() {
            return mChatInfos.size();

        }

        @Override
        public Object getItem(int position) {
            return mChatInfos.get(position);

        }

        @Override
        public long getItemId(int position) {
            return position;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(getContext(), R.layout.item_chat_msg, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.bindData(mChatInfos.get(position));
            return view;

        }
    }

    private class ViewHolder {

        ImageView iv_avatar;
        TextView tv_content;

        private ViewHolder(View view) {
            iv_avatar = view.findViewById(R.id.iv_avatar);
            tv_content = view.findViewById(R.id.tv_content);

        }

        private void bindData(ChatMsgInfo info) {
            String avatar = info.getAvatar();
            if (TextUtils.isEmpty(avatar)) {
                ImageUtils.loadRound(getContext(), R.drawable.default_avatar, iv_avatar);
            } else {
                ImageUtils.loadRound(getContext(), "https://" + avatar, iv_avatar);
            }
            tv_content.setText(info.getContent());

        }

    }

}
