package com.jd.bluedragon.external.crossbow.itms.domain;

/**
 * @ClassName ItmsResponse
 * @Description ITMS系统返回值对象
 * @Author wyh
 * @Date 2021/6/4 14:39
 **/
public class ItmsResponse<T> {

    public static final String CODE_SUCCESS = "200";

    /**
     * 状态，1：成功，2，错误，3失败
     */
    private Integer state;

    private String code;

    private String message;

    private T data;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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

    public boolean success() {
        return CODE_SUCCESS.equals(code);
    }
}
