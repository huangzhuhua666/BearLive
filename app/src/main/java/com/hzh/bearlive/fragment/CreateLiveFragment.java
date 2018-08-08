package com.hzh.bearlive.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzh.bearlive.activity.HostLiveActivity;
import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.api.BaseCallBack;
import com.hzh.bearlive.api.MyOkHttp;
import com.hzh.bearlive.app.MyApplication;
import com.hzh.bearlive.bean.ResponseObject;
import com.hzh.bearlive.bean.RoomInfo;
import com.hzh.bearlive.helper.ChoosePicHelper;
import com.hzh.bearlive.util.Constants;
import com.hzh.bearlive.util.ImageUtils;
import com.hzh.bearlive.util.ToastUtils;
import com.tencent.TIMUserProfile;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class CreateLiveFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.titleBar)
    Toolbar mTitleBar;
    @BindView(R.id.iv_cover)
    ImageView mIvCover;
    @BindView(R.id.tv_tip)
    TextView mTvTip;
    @BindView(R.id.cover)
    FrameLayout mFrameLayoutCover;
    @BindView(R.id.et_title)
    EditText mEtTitle;
    @BindView(R.id.btn_create)
    Button mBtnCreate;
    Unbinder unbinder;

    private ChoosePicHelper mChoosePicHelper;

    private String mCoverUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_live, container, false);
        unbinder = ButterKnife.bind(this, view);
        Activity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).setSupportActionBar(mTitleBar);
        }
        setListener();
        return view;

    }

    private void setListener() {
        mFrameLayoutCover.setOnClickListener(this);
        mBtnCreate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cover:
                choosePic();
                break;
            case R.id.btn_create:
                String title = mEtTitle.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    ToastUtils.showToast("请输入直播标题！");
                } else {
                    createRoom(title);
                }
                break;
            default:
                break;
        }

    }

    private void choosePic() {
        if (mChoosePicHelper == null) {
            mChoosePicHelper = new ChoosePicHelper(this, ChoosePicHelper.PicType.Cover);
        }
        mChoosePicHelper.setOnChooseResultListener(new ChoosePicHelper.OnChooseResultListener() {
            @Override
            public void onSuccess(String url) {
                updateCover(url);
            }

            @Override
            public void onFail(String msg) {
                ToastUtils.showToast("出错了！" + msg);
            }
        });
        mChoosePicHelper.showChoosePicDialog();

    }

    private void updateCover(final String url) {
        mCoverUrl = url;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageUtils.load(getContext(), "http://" + url, mIvCover);
                mTvTip.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 创建房间
     *
     * @param title 直播标题
     */
    private void createRoom(String title) {
        TIMUserProfile selfProfile = MyApplication.getSelfProfile();
        String nickname = TextUtils.isEmpty(selfProfile.getNickName()) ? selfProfile.getIdentifier()
                : selfProfile.getNickName();

        MyOkHttp.newBuilder().get().url(Constants.BASE_URL)
                .addParam("action", Constants.ACTION_CREATE)
                .addParam("userId", selfProfile.getIdentifier())
                .addParam("userName", nickname)
                .addParam("userAvatar", selfProfile.getFaceUrl())
                .addParam("liveTitle", title)
                .addParam("liveCover", mCoverUrl).build()
                .enqueue(new BaseCallBack<ResponseObject<RoomInfo>>() {
                    @Override
                    public void onSuccess(ResponseObject<RoomInfo> responseObject) {
                        if (responseObject.getCode().equals(ResponseObject.CODE_FAIL)) {
                            ToastUtils.showToast(responseObject.getErrMsg() + "错误码：" +
                                    responseObject.getErrCode());
                        } else if (responseObject.getCode().equals(ResponseObject.CODE_SUCCESS)) {
                            Intent intent = new Intent(getActivity(), HostLiveActivity.class);
                            intent.putExtra("roomId", responseObject.getData().getRoomId());
                            intent.putExtra("userId", responseObject.getData().getUserId());
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(int code) {
                        ToastUtils.showToast("服务器异常！错误码：" + code);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        ToastUtils.showToast(e.getMessage() + "错误码：-100");
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mChoosePicHelper != null) {
            mChoosePicHelper.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
