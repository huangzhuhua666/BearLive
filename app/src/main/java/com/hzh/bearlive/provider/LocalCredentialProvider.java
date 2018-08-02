package com.hzh.bearlive.provider;

import com.tencent.cos.xml.utils.StringUtils;
import com.tencent.qcloud.core.auth.BasicLifecycleCredentialProvider;
import com.tencent.qcloud.core.auth.BasicQCloudCredentials;
import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials;
import com.tencent.qcloud.core.common.QCloudClientException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 使用永久秘钥签名
 */
public class LocalCredentialProvider extends BasicLifecycleCredentialProvider {

    private String mSecretId;
    private String mSecretKey;
    private long mKeyDuration;

    public LocalCredentialProvider(String secretId, String secretKey, long keyDuration) {
        mSecretId = secretId;
        mSecretKey = secretKey;
        mKeyDuration = keyDuration;

    }

    @Override
    protected QCloudLifecycleCredentials fetchNewCredentials() throws QCloudClientException {
        long current = System.currentTimeMillis() / 1000L;
        long expired = current + mKeyDuration;
        String keyTime = current + ";" + expired;
        return new BasicQCloudCredentials(mSecretId, secretKeyToSignKey(mSecretKey, keyTime), keyTime);

    }

    /**
     * 生成签名
     *
     * @param secretKey secretKey
     * @param keyTime   keyTime
     * @return 签名
     */
    private String secretKeyToSignKey(String secretKey, String keyTime) {
        String signKey = null;
        try {
            if (secretKey == null) {
                throw new IllegalArgumentException("secretKey is null");
            }
            if (keyTime == null) {
                throw new IllegalArgumentException("qKeyTime is null");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        try {
            byte[] byteKey = secretKey.getBytes("utf-8");
            SecretKey hmacKey = new SecretKeySpec(byteKey, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(hmacKey);
            signKey = StringUtils.toHexString(mac.doFinal(keyTime.getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return signKey;

    }
}
