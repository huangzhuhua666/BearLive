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

import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.helper.ChoosePicHelper;
import com.hzh.bearlive.util.ImageUtils;
import com.hzh.bearlive.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
                    //TODO
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
