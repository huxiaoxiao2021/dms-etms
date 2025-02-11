package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;

/**
 * Created by xumei3 on 2018/2/27.
 */
public class B2BRouterResponse <T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 异常编码  **/
    public static final int CODE_EXCEPTION = 0;
    /** 正常编码  **/
    public static final int CODE_NORMAL = 1;
    /** 警告编码  **/
    public static final int CODE_WARN = 2;
    /** 失败编码  **/
    public static final int CODE_FAIL = 3;

    private T data;

    private int code;

    private String message;

    public B2BRouterResponse(){
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

}
