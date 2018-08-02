package com.hzh.bearlive.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;

import com.hzh.bearlive.activity.R;

public class EditProfileGenderDialog extends TransParentDialog {

    private RadioButton mRbMale;
    private RadioButton mRbFemale;
    private Button mBtnOk;
    private Button mBtnCancel;

    private OnOkListener mListener;

    public EditProfileGenderDialog(Activity activity) {
        super(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_profile_gender,
                null, false);
        mRbMale = view.findViewById(R.id.rb_male);
        mRbFemale = view.findViewById(R.id.rb_female);
        mBtnOk = view.findViewById(R.id.btn_ok);
        mBtnCancel = view.findViewById(R.id.btn_cancel);
        setContentView(view);
        setWidthAndHeight(activity.getWindow().getDecorView().getWidth() * 80 / 100,
                WindowManager.LayoutParams.WRAP_CONTENT);
        setOnClick();

    }

    public void show(boolean isMale) {
        mRbMale.setChecked(isMale);
        mRbFemale.setChecked(!isMale);
        show();
    }

    @Override
    protected void setOnClick() {
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    boolean isMale = mRbMale.isChecked();
                    mListener.onOk(isMale);
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

    public interface OnOkListener {

        void onOk(boolean isMale);

    }

}
