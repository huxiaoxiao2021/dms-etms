package com.jd.bluedragon.distribution.ver.exception;

/**
 * @author dudong
 * @date 2016/2/29
 */
public class SortingCheckException extends Exception {
    private Integer code;
    private String message;

    /**
     * 提示语编码
     */
    private Integer hintCode;

    public SortingCheckException(Integer code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public SortingCheckException(Integer code, String message, Integer hintCode) {
        super(message);
        this.code = code;
        this.message = message;
        this.hintCode = hintCode;
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

    public Integer getHintCode() {
        return hintCode;
    }

    public void setHintCode(Integer hintCode) {
        this.hintCode = hintCode;
    }
}
