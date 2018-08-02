package com.hzh.bearlive.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class ProfileTextView extends ProfileEdit {

    public ProfileTextView(Context context) {
        this(context, null);

    }

    public ProfileTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ProfileTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        disableEdit();

    }
}
