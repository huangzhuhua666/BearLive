package com.hzh.bearlive.bean;

public class ResponseObject<T> {

    public static final String CODE_FAIL = "0";
    public static final String CODE_SUCCESS = "1";

    private String code;
    private String errCode;
    private String errMsg;
    private T data;

    public ResponseObject() {

    }

    public ResponseObject(String code, String errCode, String errMsg, T data) {
        this.code = code;
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;

    }

    public String getCode() {
        return code;

    }

    public void setCode(String code) {
        this.code = code;

    }

    public String getErrCode() {
        return errCode;

    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;

    }

    public String getErrMsg() {
        return errMsg;

    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;

    }

    public T getData() {
        return data;

    }

    public void setData(T data) {
        this.data = data;

    }
}
