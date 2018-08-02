package com.hzh.bearlive.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hzh.bearlive.util.ToastUtils;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.titleBar)
    Toolbar mTitleBar;
    @BindView(R.id.et_account)
    EditText mEtAccount;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.et_confirm)
    EditText mEtConfirm;
    @BindView(R.id.btn_register)
    Button mBtnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setTitleBar();
        setListener();

    }

    private void setListener() {
        mBtnRegister.setOnClickListener(this);

    }

    private void setTitleBar() {
        mTitleBar.setTitle("注册新用户");
        mTitleBar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mTitleBar);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                register();
                break;
            default:
                break;
        }

    }

    /**
     * 注册
     */
    private void register() {
        String account = mEtAccount.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        String confirm = mEtConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirm)) {//信息填写为空
            ToastUtils.showToast("请填写注册所需信息！");
        } else if (account.length() < 8 || password.length() < 8) {//用户名或密码不足8位
            ToastUtils.showToast("用户名或密码长度必须大于8位！");
        } else if (!password.equals(confirm)) {//两次输入的密码不同
            ToastUtils.showToast("两次输入的密码不相同！");
        } else {//注册
            ILiveLoginManager.getInstance().tlsRegister(account, password, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {//注册成功
                    ToastUtils.showToast("注册成功，请登录！");
                    finish();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {//注册失败
                    ToastUtils.showToast("注册失败！" + errMsg);
                }
            });
        }

    }
}
