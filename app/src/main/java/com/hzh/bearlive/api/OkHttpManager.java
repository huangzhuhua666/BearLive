package com.hzh.bearlive.api;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * OkHttp管理
 */
public class OkHttpManager {

    private static OkHttpManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;
    private Gson mGson;

    private OkHttpManager() {
        initOkHttp();
        mHandler = new Handler(Looper.getMainLooper());
        mGson = new Gson();

    }

    /**
     * 单例
     *
     * @return 返回一个单例的OkHttpManager
     */
    public static OkHttpManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpManager();
                }
            }
        }
        return mInstance;

    }

    /**
     * 初始化OkHttpClient
     */
    private void initOkHttp() {
        mOkHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

    }

    /**
     * 进行请求
     *
     * @param myOkHttp MyOkHttp
     * @param callBack BaseCallBack
     */
    public void request(MyOkHttp myOkHttp, final BaseCallBack callBack) {
        if (callBack == null) {
            throw new NullPointerException("callback is null");
        }

        mOkHttpClient.newCall(myOkHttp.newRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                sendOnFailureMessage(callBack, call, e);

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {//请求成功
                    String result = response.body().string();

                    if (callBack.mType == null || callBack.mType == String.class) {//直接解析为String数据
                        sendOnSuccessMessage(callBack, result);
                    } else {
                        try {//使用Gson解析成对应的泛型的数据
                            sendOnSuccessMessage(callBack, mGson.fromJson(result, callBack.mType));
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    if (response.body() != null) {//关闭response
                        response.close();
                    }
                } else {//请求出错
                    sendOnErrorMessage(callBack, response.code());
                }

            }
        });

    }

    /**
     * 请求失败
     *
     * @param callBack BaseCallBack
     * @param call     Call
     * @param e        IOException
     */
    private void sendOnFailureMessage(final BaseCallBack callBack, final Call call,
                                      final IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onFailure(call, e);
            }
        });

    }

    /**
     * 请求错误
     *
     * @param callBack BBaseCallBack
     * @param code     状态码
     */
    private void sendOnErrorMessage(final BaseCallBack callBack, final int code) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onError(code);
            }
        });

    }

    /**
     * 请求成功
     *
     * @param callBack BaseCallBack
     * @param result   请求结果
     */
    private void sendOnSuccessMessage(final BaseCallBack callBack, final Object result) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onSuccess(result);
            }
        });

    }

}