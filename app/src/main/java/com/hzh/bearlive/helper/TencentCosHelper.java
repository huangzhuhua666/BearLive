package com.hzh.bearlive.helper;

import android.content.Context;

import com.hzh.bearlive.provider.LocalCredentialProvider;
import com.hzh.bearlive.util.Constants;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.object.PutObjectRequest;

/**
 * 腾讯云对象存储工具
 */
public class TencentCosHelper {

    private static CosXmlService mCosXmlService;

    /**
     * 请在Application中初始化腾讯云对象存储
     *
     * @param context 应用的上下文
     */
    public static void initCos(Context context) {
        //创建 CosXmlServiceConfig 对象
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(Constants.COS_APP_ID, Constants.COS_REGION)
                .setDebuggable(true)
                .builder();

        //创建获取签名类
        LocalCredentialProvider localCredentialProvider = new LocalCredentialProvider(
                Constants.COS_SECRET_ID, Constants.COS_SECRET_KEY, Constants.COS_DURATION);

        //创建 CosXmlService 对象，实现对象存储服务各项操作
        mCosXmlService = new CosXmlService(context, serviceConfig, localCredentialProvider);

    }

    /**
     * 上传图片到腾讯云
     *
     * @param srcPath  图片绝对路径
     * @param isAvatar 是否为头像 true：头像  false：封面
     * @param listener 监听回调
     */
    public static void uploadPic(String srcPath, boolean isAvatar, CosXmlResultListener listener) {
        //cos的绝对路径
        String cosPath;
        if (isAvatar) {
            cosPath = "avatar/";
        } else {
            cosPath = "cover/";
        }
        cosPath += srcPath.substring(srcPath.lastIndexOf("/") + 1);

        PutObjectRequest putObjectRequest = new PutObjectRequest(Constants.COS_BUCKET, cosPath, srcPath);
        putObjectRequest.setSign(Constants.COS_DURATION, null, null);
        mCosXmlService.putObjectAsync(putObjectRequest, listener);

    }

}
