package com.hzh.bearlive.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;

import com.hzh.bearlive.activity.MainActivity;
import com.hzh.bearlive.activity.R;
import com.hzh.bearlive.app.MyApplication;
import com.hzh.bearlive.helper.ChoosePicHelper;
import com.hzh.bearlive.util.Constants;
import com.hzh.bearlive.util.CustomProfiles;
import com.hzh.bearlive.util.ImageUtils;
import com.hzh.bearlive.util.SpUtils;
import com.hzh.bearlive.util.ToastUtils;
import com.hzh.bearlive.view.EditProfileGenderDialog;
import com.hzh.bearlive.view.EditProfileNormalDialog;
import com.hzh.bearlive.view.ProfileEdit;
import com.hzh.bearlive.view.ProfileTextView;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditProfileFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.titleBar)
    Toolbar mTitleBar;
    @BindView(R.id.avatar)
    View mAvatarView;
    @BindView(R.id.avatar_img)
    ImageView mAvatarImg;
    @BindView(R.id.nick_name)
    ProfileEdit mNickName;
    @BindView(R.id.gender)
    ProfileEdit mGender;
    @BindView(R.id.sign)
    ProfileEdit mSign;
    @BindView(R.id.identification)
    ProfileEdit mIdentification;
    @BindView(R.id.location)
    ProfileEdit mLocation;
    @BindView(R.id.id_num)
    ProfileTextView mIdNum;
    @BindView(R.id.level)
    ProfileTextView mLevel;
    @BindView(R.id.get_num)
    ProfileTextView mGetNum;
    @BindView(R.id.send_num)
    ProfileTextView mSendNum;
    @BindView(R.id.btn_complete)
    Button mBtnComplete;
    Unbinder unbinder;

    private ChoosePicHelper mChoosePicHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        setTitleBar();
        setListener();
        getSelfInfo();
        return view;

    }

    /**
     * 获取个人信息
     */
    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {//获取信息失败
                ToastUtils.showToast("获取信息失败！");
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {//获取信息成功
                MyApplication.setSelfProfile(timUserProfile);
                updateViews(timUserProfile);
            }
        });

    }

    /**
     * 显示个人信息
     *
     * @param timUserProfile timUserProfile
     */
    private void updateViews(TIMUserProfile timUserProfile) {
        //显示头像
        String faceUrl = "https://" + timUserProfile.getFaceUrl();
        if (TextUtils.isEmpty(faceUrl)) {//加载默认头像
            ImageUtils.loadRound(getContext(), R.drawable.default_avatar, mAvatarImg);
        } else {//加载自己添加的头像
            ImageUtils.loadRound(getContext(), faceUrl, mAvatarImg);
        }
        //显示昵称
        mNickName.updateValue(timUserProfile.getNickName());
        //显示性别
        long genderValue = timUserProfile.getGender().getValue();
        mGender.updateValue(genderValue == 1 ? "男" : "女");
        //显示个性签名
        mSign.updateValue(timUserProfile.getSelfSignature());
        //显示地区信息
        mLocation.updateValue(timUserProfile.getLocation());
        //显示ID
        mIdNum.updateValue(timUserProfile.getIdentifier());

        Map<String, byte[]> customInfo = timUserProfile.getCustomInfo();
        //显示认证信息
        mIdentification.updateValue(getValue(customInfo, CustomProfiles.CUSTOM_RENZHENG, "未知"));
        //显示等级
        mLevel.updateValue(getValue(customInfo, CustomProfiles.CUSTOM_LEVEL, "0"));
        //显示获得的票数
        mGetNum.updateValue(getValue(customInfo, CustomProfiles.CUSTOM_GET, "0"));
        //显示送出的票数
        mSendNum.updateValue(getValue(customInfo, CustomProfiles.CUSTOM_SEND, "0"));

    }

    /**
     * 获取CustomInfo里的信息
     *
     * @param customInfo   customInfo
     * @param key          key
     * @param defaultValue 默认值
     * @return 对应信息
     */
    private String getValue(Map<String, byte[]> customInfo, String key, String defaultValue) {
        if (customInfo != null) {
            byte[] valueByte = customInfo.get(key);
            if (valueByte != null) {
                return new String(valueByte);
            }
        }
        return defaultValue;

    }

    private void setListener() {
        mAvatarView.setOnClickListener(this);
        mNickName.setOnClickListener(this);
        mGender.setOnClickListener(this);
        mSign.setOnClickListener(this);
        mIdentification.setOnClickListener(this);
        mLocation.setOnClickListener(this);
        mBtnComplete.setOnClickListener(this);

    }

    private void setTitleBar() {
        mTitleBar.setTitle("编辑个人信息");
        mTitleBar.setTitleTextColor(Color.WHITE);
        Activity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).setSupportActionBar(mTitleBar);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                //修改头像
                choosePic();
                break;
            case R.id.nick_name:
                //修改昵称
                showEditNormalDialog("昵称", R.drawable.ic_info_nickname, mNickName.getValue());
                break;
            case R.id.gender:
                //修改性别
                showEditGenderDialog();
                break;
            case R.id.sign:
                //修改个性签名
                showEditNormalDialog("个性签名", R.drawable.ic_info_sign, mSign.getValue());
                break;
            case R.id.identification:
                //修改认证
                showEditNormalDialog("认证", R.drawable.ic_info_renzhen, mIdentification.getValue());
                break;
            case R.id.location:
                //修改地区
                showEditNormalDialog("地区", R.drawable.ic_info_location, mLocation.getValue());
                break;
            case R.id.btn_complete:
                //跳转主界面
                SpUtils.putBoolean(getContext(), Constants.IS_FIRST_LOGIN, false);
                startActivity(new Intent(getActivity(), MainActivity.class));
                Activity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
                break;
            default:
                break;
        }

    }

    /**
     * 选择头像
     */
    private void choosePic() {
        if (mChoosePicHelper == null) {
            mChoosePicHelper = new ChoosePicHelper(this, ChoosePicHelper.PicType.Avatar);
            mChoosePicHelper.setOnChooseResultListener(new ChoosePicHelper.OnChooseResultListener() {
                @Override
                public void onSuccess(String url) {
                    updateAvatar(url);
                }

                @Override
                public void onFail(String msg) {
                    ToastUtils.showToast("出错了！" + msg);
                }
            });
        }
        mChoosePicHelper.showChoosePicDialog();

    }

    /**
     * 显示修改个人信息的Dialog
     *
     * @param title          title
     * @param redId          resId
     * @param defaultContent 默认的内容
     */
    private void showEditNormalDialog(final String title, int redId, String defaultContent) {
        EditProfileNormalDialog dialog = new EditProfileNormalDialog(getActivity());
        dialog.setDialogListener(new EditProfileNormalDialog.OnOkListener() {
            @Override
            public void onOk(final String content) {
                switch (title) {
                    case "昵称":
                        //修改昵称
                        TIMFriendshipManager.getInstance().setNickName(content, new TIMCallBack() {
                            @Override
                            public void onError(int i, String s) {//修改失败
                                ToastUtils.showToast(s + i);
                            }

                            @Override
                            public void onSuccess() {//修改成功
                                mNickName.updateValue(content);
                            }
                        });
                        break;
                    case "个性签名":
                        //修改个性签名
                        TIMFriendshipManager.getInstance().setSelfSignature(content, new TIMCallBack() {
                            @Override
                            public void onError(int i, String s) {//修改失败
                                ToastUtils.showToast(s + i);
                            }

                            @Override
                            public void onSuccess() {//修改成功
                                mSign.updateValue(content);
                            }
                        });
                        break;
                    case "认证":
                        //修改认证
                        TIMFriendshipManager.getInstance().setCustomInfo(CustomProfiles.CUSTOM_RENZHENG,
                                content.getBytes(), new TIMCallBack() {
                                    @Override
                                    public void onError(int i, String s) {//修改失败
                                        ToastUtils.showToast(s + i);
                                    }

                                    @Override
                                    public void onSuccess() {//修改成功
                                        mIdentification.updateValue(content);
                                    }
                                });
                        break;
                    case "地区":
                        //修改地区
                        TIMFriendshipManager.getInstance().setLocation(content, new TIMCallBack() {
                            @Override
                            public void onError(int i, String s) {//修改失败
                                ToastUtils.showToast(s + i);
                            }

                            @Override
                            public void onSuccess() {//修改成功
                                mLocation.updateValue(content);
                            }
                        });
                        break;
                    default:
                        break;
                }

            }
        });
        dialog.show(title, redId, defaultContent);

    }

    /**
     * 显示修改性别的Dialog
     */
    private void showEditGenderDialog() {
        EditProfileGenderDialog dialog = new EditProfileGenderDialog(getActivity());
        dialog.setDialogListener(new EditProfileGenderDialog.OnOkListener() {
            @Override
            public void onOk(final boolean isMale) {
                TIMFriendGenderType gender = isMale ? TIMFriendGenderType.Male : TIMFriendGenderType.Female;
                TIMFriendshipManager.getInstance().setGender(gender, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {//修改失败
                        ToastUtils.showToast(s + i);
                    }

                    @Override
                    public void onSuccess() {//修改成功
                        mGender.updateValue(isMale ? "男" : "女");
                    }
                });
            }
        });
        dialog.show(mGender.getValue().equals("男"));

    }

    /**
     * 更新头像
     *
     * @param url rul
     */
    private void updateAvatar(final String url) {
        TIMFriendshipManager.getInstance().setFaceUrl(url, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                //更新头像失败
                ToastUtils.showToast(s + i);
            }

            @Override
            public void onSuccess() {
                //更新头像成功
                ImageUtils.loadRound(getContext(), url, mAvatarImg);
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
