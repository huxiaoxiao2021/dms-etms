package com.jd.bluedragon.distribution.jy.exception;

/**
 * 拣运降级自定义异常
 *
 * @author hujiping
 * @date 2022/10/10 2:26 PM
 */
public class JyDemotionException extends RuntimeException {

    public JyDemotionException() {
    }

    public JyDemotionException(String message) {
        super(message);
    }
}
