package com.hzh.bearlive.util;

import android.widget.Toast;

import com.hzh.bearlive.app.MyApplication;

/**
 * Toast工具
 */
public class ToastUtils {

    private static Toast mToast;

    /**
     * 显示默认时长（Short）Toast
     *
     * @param msg msg
     */
    public static void showToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);

    }

    /**
     * 显示默认时长（Short）Toast
     *
     * @param resId resId
     */
    public static void showToast(int resId) {
        showToast(resId, Toast.LENGTH_SHORT);
    }

    /**
     * 显示Toast
     *
     * @param msg      msg
     * @param duration 时长
     */
    public static void showToast(String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(MyApplication.getContext(), "", duration);
        }
        mToast.setText(msg);
        mToast.show();

    }

    /**
     * 显示Toast
     *
     * @param resId    resId
     * @param duration 时长
     */
    public static void showToast(int resId, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(MyApplication.getContext(), "", duration);
        }
        mToast.setText(resId);
        mToast.show();

    }

}
