package com.hzh.bearlive.listener;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * 软键盘状态监听
 */
public class SoftKeyboardListener {

    //activity的根视图
    private View rootView;
    //纪录根视图的显示高度
    private int rootViewVisibleHeight;
    private OnSoftKeyboardChangeListener mListener;

    private SoftKeyboardListener(Activity activity) {
        //获取activity的根视图
        rootView = activity.getWindow().getDecorView();
        //监听视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获取当前根视图在屏幕上显示的大小
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int visibleHeight = r.height();
                if (rootViewVisibleHeight == 0) {
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }
                //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
                if (rootViewVisibleHeight == visibleHeight) {
                    return;
                }
                if (mListener != null) {
                    //根视图显示高度变小超过200，可以看作软键盘显示了
                    if (rootViewVisibleHeight - visibleHeight > 200) {
                        mListener.onShow();
                    }
                    //根视图显示高度变大超过200，可以看作软键盘隐藏了
                    if (visibleHeight - rootViewVisibleHeight > 200) {
                        mListener.onHide();
                    }
                    rootViewVisibleHeight = visibleHeight;
                }
            }
        });

    }

    private void setListener(OnSoftKeyboardChangeListener listener) {
        mListener = listener;

    }

    public static void setListener(Activity activity, OnSoftKeyboardChangeListener listener) {
        SoftKeyboardListener softKeyboardListener = new SoftKeyboardListener(activity);
        softKeyboardListener.setListener(listener);

    }

    public interface OnSoftKeyboardChangeListener {

        void onShow();

        void onHide();

    }

}
