package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;

public class DmsBaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_FAILED = 500;

    public static final String MESSAGE_SUCCESS = "成功";
    public static final String MESSAGE_FAILED_NO_PARAMS = "参数为空";
    /** 相应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;

    private T data;

    public DmsBaseResponse() {
    }

    public DmsBaseResponse(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
