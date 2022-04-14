package com.jd.bluedragon.distribution.ministore.exception;

import com.jd.bluedragon.common.dto.base.response.RespCodeMapping;

public class MiniStoreBizException extends RuntimeException{

    private Integer code;
    private String message;

    public MiniStoreBizException(Integer code,String message){
        super(message);
        this.code =code;
        this.message =message;
    }

    public MiniStoreBizException(RespCodeMapping respCodeMapping){
        super(respCodeMapping.getMessage());
        this.code = respCodeMapping.getCode();
        this.message = respCodeMapping.getMessage();
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
