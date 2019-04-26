package com.jd.bluedragon.distribution.exception.jss;

/**
 * @author lixin39
 * @Description JSS存储异常
 * @ClassName JssStorageException
 * @date 2019/4/18
 */
public class JssStorageException extends RuntimeException{

    public JssStorageException(String message) {
        super(message);
    }

    public JssStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
