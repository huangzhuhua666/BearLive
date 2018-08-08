package com.hzh.bearlive.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.activity.WatcherActivity;
import com.hzh.bearlive.api.BaseCallBack;
import com.hzh.bearlive.api.MyOkHttp;
import com.hzh.bearlive.bean.ResponseObject;
import com.hzh.bearlive.bean.RoomInfo;
import com.hzh.bearlive.util.Constants;
import com.hzh.bearlive.util.ImageUtils;
import com.hzh.bearlive.util.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class LiveListFragment extends Fragment {

    @BindView(R.id.titleBar)
    Toolbar mTitleBar;
    @BindView(R.id.live_list)
    ListView mLiveList;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    Unbinder unbinder;

    private LiveListAdapter mAdapter;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        Activity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).setSupportActionBar(mTitleBar);
        }
        initData();
        setListener();
        return view;

    }

    private void initData() {
        mContext = getContext();
        mAdapter = new LiveListAdapter();
        mLiveList.setAdapter(mAdapter);
        mSwipeRefresh.setRefreshing(true);
        requestLiveList();

    }

    private void setListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh.setRefreshing(true);
                //请求服务器，获取直播列表
                requestLiveList();
            }
        });

    }

    private void requestLiveList() {
        //请求前20个数据
        MyOkHttp.newBuilder().get().url(Constants.BASE_URL)
                .addParam("action", Constants.ACTION_GET_LIST)
                .addParam("pageIndex", 0 + "").build()
                .enqueue(new BaseCallBack<ResponseObject<List<RoomInfo>>>() {
                    @Override
                    public void onSuccess(ResponseObject<List<RoomInfo>> responseObject) {
                        if (responseObject.getCode().equals(ResponseObject.CODE_FAIL)) {
                            ToastUtils.showToast(responseObject.getErrMsg() + "错误码：" +
                                    responseObject.getErrCode());
                        } else if (responseObject.getCode().equals(ResponseObject.CODE_SUCCESS)) {
                            mAdapter.removeAllRoom();//下拉刷新，先移除掉之前的room信息
                            mAdapter.updateRoom(responseObject.getData());
                            mSwipeRefresh.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onError(int code) {
                        ToastUtils.showToast("服务器异常！错误码：" + code);
                        mSwipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        ToastUtils.showToast(e.getMessage() + "错误码：-100");
                        mSwipeRefresh.setRefreshing(false);
                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }


    private class LiveListAdapter extends BaseAdapter {

        private List<RoomInfo> mRoomList;

        private LiveListAdapter() {
            mRoomList = new ArrayList<>();

        }

        /**
         * 移除所有数据
         */
        public void removeAllRoom() {
            mRoomList.clear();

        }

        /**
         * 更新直播房间列表
         *
         * @param roomList 直播房间列表
         */
        public void updateRoom(List<RoomInfo> roomList) {
            if (roomList != null) {
                mRoomList.clear();
                mRoomList.addAll(roomList);
                notifyDataSetChanged();
            }

        }

        @Override
        public int getCount() {
            return mRoomList.size();

        }

        @Override
        public Object getItem(int position) {
            return mRoomList.get(position);

        }

        @Override
        public long getItemId(int position) {
            return position;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            final RoomInfo roomInfo = mRoomList.get(position);
            if (convertView == null) {
                view = View.inflate(mContext, R.layout.item_live_list, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.bindData(roomInfo);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WatcherActivity.class);
                    intent.putExtra("roomId", roomInfo.getRoomId());
                    intent.putExtra("hostId", roomInfo.getUserId());
                    startActivity(intent);
                }
            });
            return view;

        }

    }

    private class ViewHolder {

        TextView liveTitle;
        ImageView liveCover;
        ImageView hostAvatar;
        TextView hostName;
        TextView watcherNum;

        private ViewHolder(View view) {
            liveTitle = view.findViewById(R.id.live_title);
            liveCover = view.findViewById(R.id.live_cover);
            hostAvatar = view.findViewById(R.id.host_avatar);
            hostName = view.findViewById(R.id.host_name);
            watcherNum = view.findViewById(R.id.watcher_num);

        }

        private void bindData(RoomInfo roomInfo) {
            //直播标题
            liveTitle.setText(roomInfo.getLiveTitle());
            //直播封面
            String cover = roomInfo.getLiveCover();
            if (TextUtils.isEmpty(cover)) {
                ImageUtils.load(mContext, R.drawable.default_cover, liveCover);
            } else {
                ImageUtils.load(mContext, cover, liveCover);
            }
            //主播头像
            String avatar = roomInfo.getUserAvatar();
            if (TextUtils.isEmpty(avatar)) {
                ImageUtils.loadRound(mContext, R.drawable.default_avatar, hostAvatar);
            } else {
                ImageUtils.loadRound(mContext, avatar, hostAvatar);
            }
            //主播名字
            hostName.setText(TextUtils.isEmpty(roomInfo.getUserNickname()) ? roomInfo.getUserId() :
                    roomInfo.getUserNickname());
            //观看人数
            watcherNum.setText(roomInfo.getWatcherNum() + "人\r\n正在看");

        }

    }

}
