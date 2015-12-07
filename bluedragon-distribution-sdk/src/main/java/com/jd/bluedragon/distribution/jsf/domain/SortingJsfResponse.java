package com.jd.bluedragon.distribution.jsf.domain;

public class SortingJsfResponse {

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
