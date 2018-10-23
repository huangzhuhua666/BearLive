package com.hzh.bearlive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hzh.bearlive.app.MyApplication;
import com.hzh.bearlive.util.Constants;
import com.hzh.bearlive.util.SpUtils;
import com.hzh.bearlive.util.ToastUtils;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.et_account)
    EditText mEtAccount;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    @BindView(R.id.btn_register)
    Button mBtnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setListener();

    }

    private void setListener() {
        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            default:
                break;
        }

    }

    /**
     * 登录
     */
    private void login() {
        final String account = mEtAccount.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
            ToastUtils.showToast("用户名或密码不能为空！");
        } else {//登录
            ILiveLoginManager.getInstance().tlsLogin(account, password, new ILiveCallBack<String>() {
                @Override
                public void onSuccess(String data) {//登录成功
                    ILiveLoginManager.getInstance().iLiveLogin(account, data, new ILiveCallBack() {
                        @Override
                        public void onSuccess(Object data) {//登录成功
                            if (SpUtils.getBoolean(LoginActivity.this,
                                    Constants.IS_FIRST_LOGIN, true)) {//首次登陆，进入编辑个人信息界面
                                startActivity(new Intent(LoginActivity.this,
                                        EditProfileActivity.class));
                            } else {//非首次登陆，直接进入主界面
                                TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
                                    @Override
                                    public void onError(int i, String s) {
                                        ToastUtils.showToast("获取信息失败！");
                                    }

                                    @Override
                                    public void onSuccess(TIMUserProfile userProfile) {
                                        MyApplication.setSelfProfile(userProfile);
                                    }
                                });
                                startActivity(new Intent(LoginActivity.this,
                                        MainActivity.class));
                            }
                            finish();
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {//登录失败
                            ToastUtils.showToast("登录失败！" + errMsg);
                        }
                    });
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {//登录失败
                    ToastUtils.showToast("登录失败！" + errMsg);
                }
            });
        }

    }
}
