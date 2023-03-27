package com.jd.bluedragon.distribution.jy.dto;

/**
 * 拣运-业务自定义异常
 *
 * @author hujiping
 * @date 2023/4/4 1:56 PM
 */
public class JyBusinessException extends RuntimeException {
    
    public JyBusinessException(String message){
        super(message);
    }

    // avoid the expensive and useless stack trace for this exceptions
    public Throwable fillInStackTrace() {
        return this;
    }
}
