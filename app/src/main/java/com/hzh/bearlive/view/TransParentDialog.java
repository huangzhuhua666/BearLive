package com.hzh.bearlive.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hzh.bearlive.activity.R;

public class TransParentDialog {

    protected Activity mActivity;
    protected Dialog mDialog;

    protected TransParentDialog(Activity activity, boolean isNoDim) {
        mActivity = activity;
        if (isNoDim) {
            mDialog = new Dialog(activity, R.style.dialog_nodim);
        } else {
            mDialog = new Dialog(activity, R.style.dialog);
        }

    }

    protected void setContentView(View view) {
        mDialog.setContentView(view);

    }

    protected void setWidthAndHeight(int width, int height) {
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (params != null) {
            params.width = width;
            params.height = height;
            window.setAttributes(params);
        }

    }

    protected void show() {
        mDialog.show();

    }

    protected void dismiss() {
        mDialog.dismiss();

    }

    protected void setOnClick() {

    }

}
