package com.hzh.bearlive.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hzh.bearlive.activity.R;

public class EditProfileNormalDialog extends TransParentDialog {

    private TextView mTvTitle;
    private EditText mEtContent;
    private Button mBtnOk;
    private Button mBtnCancel;

    private OnOkListener mListener;

    public EditProfileNormalDialog(Activity activity) {
        super(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_edti_profile_normal,
                null, false);
        mTvTitle = view.findViewById(R.id.tv_title);
        mEtContent = view.findViewById(R.id.et_content);
        mBtnOk = view.findViewById(R.id.btn_ok);
        mBtnCancel = view.findViewById(R.id.btn_cancel);
        setContentView(view);
        setWidthAndHeight(activity.getWindow().getDecorView().getWidth() * 80 / 100,
                WindowManager.LayoutParams.WRAP_CONTENT);
        setOnClick();

    }

    @Override
    protected void setOnClick() {
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    String content = mEtContent.getText().toString();
                    mListener.onOk(content);
                }
                dismiss();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    public void setDialogListener(OnOkListener listener) {
        mListener = listener;

    }

    public void show(String title, int redId, String defaultContent) {
        mTvTitle.setText("请输入" + title);
        mEtContent.setCompoundDrawablesWithIntrinsicBounds(redId, 0, 0, 0);
        mEtContent.setText(defaultContent);
        show();

    }

    public interface OnOkListener {

        void onOk(String content);

    }

}
