package com.jd.bluedragon.distribution.ministore.exception;

import com.jd.bluedragon.common.dto.base.response.ResponseCodeMapping;

public class MiniStoreBizException extends RuntimeException{

    private Integer code;
    private String message;

    public MiniStoreBizException(Integer code,String message){
        super(message);
        this.code =code;
        this.message =message;
    }

    public MiniStoreBizException(ResponseCodeMapping responseCodeMapping){
        super(responseCodeMapping.getMessage());
        this.code =responseCodeMapping.getCode();
        this.message =responseCodeMapping.getMessage();
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
