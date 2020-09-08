package com.jd.bluedragon.distribution.ver.exception;

/**
 * @author dudong
 * @date 2016/2/29
 */
public class SortingCheckException extends Exception {
    private Integer code;
    private String message;

    public SortingCheckException(Integer code, String message){
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
