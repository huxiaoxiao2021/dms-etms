package com.jd.bluedragon.distribution.ver.exception;

/**
 * 运单号非法
 * Created by shipeilin on 2017/11/15.
 */
public class IllegalWayBillCodeException extends RuntimeException {
    private Integer code;
    private String message;

    public IllegalWayBillCodeException(String message){
        super(message);
        this.message = message;
    }

    public IllegalWayBillCodeException(Integer code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
