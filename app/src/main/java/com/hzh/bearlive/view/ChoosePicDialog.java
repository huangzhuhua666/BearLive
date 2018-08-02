package com.hzh.bearlive.view;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.hzh.bearlive.activity.R;

public class ChoosePicDialog extends TransParentDialog {

    private Button mBtnCamera;
    private Button mBtnAlbum;
    private ImageView mIvCancel;

    private OnChooseListener mListener;

    public ChoosePicDialog(Activity activity) {
        super(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_choose_pic,
                null, false);
        setContentView(view);
        setWidthAndHeight(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mBtnCamera = view.findViewById(R.id.btn_camera);
        mBtnAlbum = view.findViewById(R.id.btn_album);
        mIvCancel = view.findViewById(R.id.btn_cancel);
        setOnClick();

    }

    public void setOnChooseListener(OnChooseListener listener) {
        mListener = listener;

    }

    @Override
    protected void setOnClick() {
        mBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCamera();
                }
                dismiss();
            }
        });

        mBtnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onAlbum();
                }
                dismiss();
            }
        });

        mIvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    public interface OnChooseListener {

        void onCamera();

        void onAlbum();

    }

    public void show() {
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (params != null) {
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
        }
        super.show();

    }
}
