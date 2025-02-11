package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;

public class RmaHandoverResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异常编码
     **/
    public static final int CODE_EXCEPTION = 0;
    /**
     * 正常编码
     **/
    public static final int CODE_NORMAL = 1;
    /**
     * 警告编码
     **/
    public static final int CODE_WARN = 2;
    /**
     * 失败编码
     **/
    public static final int CODE_FAIL = 3;

    private T data;

    private int code;

    private String message;

    public RmaHandoverResponse() {
    }

    public RmaHandoverResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void init(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void toNormal(String message) {
        this.init(RmaHandoverResponse.CODE_NORMAL, message);
    }

    public void toException(String message) {
        this.init(RmaHandoverResponse.CODE_EXCEPTION, message);
    }

    public void toFail(String message) {
        this.init(RmaHandoverResponse.CODE_FAIL, message);
    }

    public void toWarn(String message) {
        this.init(RmaHandoverResponse.CODE_WARN, message);
    }

}
