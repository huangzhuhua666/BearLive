package com.hzh.bearlive.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzh.bearlive.activity.R;

public class ProfileEdit extends LinearLayout {

    private ImageView mIvIcon;
    private TextView mTvKey;
    private TextView mTvValue;
    private ImageView mIvArrow;

    public ProfileEdit(Context context) {
        this(context, null);

    }

    public ProfileEdit(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ProfileEdit(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttrs(context, attrs);

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_profile, this, true);

        mIvIcon = findViewById(R.id.profile_icon);
        mTvKey = findViewById(R.id.profile_key);
        mTvValue = findViewById(R.id.profile_value);
        mIvArrow = findViewById(R.id.right_arrow);

    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProfileEdit,
                0, 0);
        mIvIcon.setImageResource(ta.getResourceId(R.styleable.ProfileEdit_icon, -1));
        mTvKey.setText(ta.getString(R.styleable.ProfileEdit_key));
        mTvValue.setText(ta.getString(R.styleable.ProfileEdit_value));
        ta.recycle();

    }

    public void set(int iconResId, String key, String value) {
        mIvIcon.setImageResource(iconResId);
        mTvKey.setText(key);
        mTvValue.setText(value);

    }

    public void updateValue(String value) {
        mTvValue.setText(value);

    }

    public String getValue() {
        return mTvValue.getText().toString();
    }

    public void disableEdit() {
        mIvArrow.setVisibility(GONE);

    }
}
