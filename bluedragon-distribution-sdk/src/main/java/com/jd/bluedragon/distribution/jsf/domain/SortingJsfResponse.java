package com.jd.bluedragon.distribution.jsf.domain;

public class SortingJsfResponse {

    public static final Integer CODE_SERVICE_ERROR = 20000;
    public static final String MESSAGE_SERVICE_ERROR = "service exceptions";
    public static final String MESSAGE_SERVICE_ERROR_C = "服务异常!";

    /** 相应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;

    public SortingJsfResponse() {
    }

    public SortingJsfResponse(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
