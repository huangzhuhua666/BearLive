package com.hzh.bearlive.api;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 网络请求工具
 */
public class MyOkHttp {

    private Builder mBuilder;

    private MyOkHttp(Builder builder) {
        mBuilder = builder;

    }

    public static Builder newBuilder() {
        return new Builder();

    }

    /**
     * 构造Request
     *
     * @return request
     */
    public Request newRequest() {
        Request.Builder builder = new Request.Builder();

        if (mBuilder.method.equals("GET")) {//get方法
            builder.get().url(buildGetRequestParam());
        } else if (mBuilder.method.equals("POST")) {//post方法
            try {
                builder.post(buildRequestBody()).url(mBuilder.url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return builder.build();

    }

    /**
     * 构造get请求的url
     *
     * @return url
     */
    private String buildGetRequestParam() {
        //没有请求的参数，直接返回url
        if (mBuilder.mParams == null || mBuilder.mParams.size() <= 0) {
            return mBuilder.url;
        }
        //解析请求参数，拼接到url上
        Uri.Builder builder = Uri.parse(mBuilder.url).buildUpon();

        for (RequestParam param : mBuilder.mParams) {
            builder.appendQueryParameter(param.getKey(),
                    param.getObj() == null ? "" : param.getObj().toString());
        }
        return builder.build().toString();

    }

    /**
     * 构造post请求的RequestBody
     *
     * @return RequestBody
     * @throws JSONException JSONException
     */
    private RequestBody buildRequestBody() throws JSONException {
        if (mBuilder.isJSON) {//请求的是JSON数据形式
            JSONObject object = new JSONObject();

            for (RequestParam param : mBuilder.mParams) {
                object.put(param.getKey(), param.getObj());
            }
            String json = object.toString();
            return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        }

        //请求的是Form表单形式
        FormBody.Builder builder = new FormBody.Builder();

        for (RequestParam param : mBuilder.mParams) {
            builder.add(param.getKey(), param.getObj() == null ? "" : param.getObj().toString());
        }
        return builder.build();

    }

    /**
     * 异步执行请求
     *
     * @param callBack BaseCallBack
     */
    public void enqueue(BaseCallBack callBack) {
        OkHttpManager.getInstance().request(this, callBack);

    }

    public static class Builder {

        private String url;
        private String method;
        private List<RequestParam> mParams;
        private boolean isJSON;

        private Builder() {
            method = "GET";
            isJSON = false;
        }

        /**
         * 传url
         *
         * @param url url
         * @return Builder
         */
        public Builder url(String url) {
            this.url = url;
            return this;

        }

        /**
         * get方法
         *
         * @return Builder
         */
        public Builder get() {
            method = "GET";
            return this;

        }

        /**
         * 提交Form表单形式的post方法
         *
         * @return Builder
         */
        public Builder post() {
            method = "POST";
            return this;

        }

        /**
         * 提交JSON数据的post方法
         *
         * @return Builder
         */
        public Builder json() {
            isJSON = true;
            return post();

        }

        /**
         * 添加请求参数
         *
         * @param key   key
         * @param value value
         * @return Builder
         */
        public Builder addParam(String key, Object value) {
            if (mParams == null) {
                mParams = new ArrayList<>();
                mParams.add(new RequestParam(key, value));
            } else {
                mParams.add(new RequestParam(key, value));
            }
            return this;

        }

        public MyOkHttp build() {
            return new MyOkHttp(this);
        }

    }

}
