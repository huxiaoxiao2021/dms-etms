package com.jd.bluedragon.distribution.capability.send.exce;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/9/7
 * @Description:
 */
public class SendOfCapabilityAreaException extends RuntimeException{

    public SendOfCapabilityAreaException() {
    }

    public SendOfCapabilityAreaException(String message) {
        super(message);
    }

    public SendOfCapabilityAreaException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendOfCapabilityAreaException(Throwable cause) {
        super(cause);
    }

    public SendOfCapabilityAreaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
