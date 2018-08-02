package com.hzh.bearlive.app;

import android.app.Application;
import android.content.Context;

import com.hzh.bearlive.helper.TencentCosHelper;
import com.hzh.bearlive.util.Constants;
import com.hzh.bearlive.util.CustomProfiles;
import com.tencent.TIMManager;
import com.tencent.TIMUserProfile;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private static Context mContext;
    private static ILVLiveConfig mLiveConfig;
    private static TIMUserProfile mSelfProfile;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initILiveSDK();
        TencentCosHelper.initCos(mContext);
    }

    /**
     * 初始化腾讯互动直播的SDK
     */
    private void initILiveSDK() {
        //ILiveSDK初始化
        ILiveSDK.getInstance().initSdk(mContext, Constants.ILIVE_APP_ID, Constants.ILIVE_ACCOUNT_TYPE);

        //初始化直播场景
        mLiveConfig = new ILVLiveConfig();
        ILVLiveManager.getInstance().init(mLiveConfig);

        //自定义字段
        List<String> customInfo = new ArrayList<>();
        customInfo.add(CustomProfiles.CUSTOM_RENZHENG);
        customInfo.add(CustomProfiles.CUSTOM_LEVEL);
        customInfo.add(CustomProfiles.CUSTOM_GET);
        customInfo.add(CustomProfiles.CUSTOM_SEND);

        //初始化个人信息设置的字段
        TIMManager.getInstance().initFriendshipSettings(CustomProfiles.allBaseInfo, customInfo);

    }

    /**
     * 获取Application全局Context
     *
     * @return context
     */
    public static Context getContext() {
        return mContext;

    }

    public static void setSelfProfile(TIMUserProfile userProfile) {
        mSelfProfile = userProfile;

    }

    public static TIMUserProfile getSelfProfile() {
        return mSelfProfile;

    }

    public static ILVLiveConfig getLiveConfig() {
        return mLiveConfig;

    }
}
