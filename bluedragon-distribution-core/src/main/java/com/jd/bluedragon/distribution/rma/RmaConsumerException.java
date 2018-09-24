package com.jd.bluedragon.distribution.rma;

/**
 * <p>
 * Created by lixin39 on 2018/9/24.
 */
public class RmaConsumerException extends RuntimeException {
    public RmaConsumerException() {
    }

    public RmaConsumerException(String message) {
        super(message);
    }

    public RmaConsumerException(String message, Throwable cause) {
        super(message, cause);
    }
}
