package com.hzh.bearlive.api;

/**
 * 请求参数
 */
public class RequestParam {

    private String key;
    private Object obj;

    public RequestParam(String key, Object obj) {
        this.key = key;
        this.obj = obj;

    }

    public String getKey() {
        return key;

    }

    public void setKey(String key) {
        this.key = key;

    }

    public Object getObj() {
        return obj;

    }

    public void setObj(Object obj) {
        this.obj = obj;

    }
}
