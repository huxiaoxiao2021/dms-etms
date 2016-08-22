package com.jd.bluedragon.distribution.crossbox.domain;

import java.io.Serializable;

/**
 * Created by xumei1 on 2016/8/9.
 */
public class CrossBoxResult<T> implements Serializable {
    public static final int SUCCESS = 0; //成功
    public static final int FAIL = -1; //失败
    private T data; //返回数据
    private int resultCode;
    private String message;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CrossBoxResult(int resultCode, T data, String message) {
        this.resultCode = resultCode;
        this.data = data;
        this.message = message;
    }
    public CrossBoxResult() {
    }
}
