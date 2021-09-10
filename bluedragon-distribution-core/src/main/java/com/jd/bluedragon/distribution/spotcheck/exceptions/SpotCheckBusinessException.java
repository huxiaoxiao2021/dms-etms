package com.jd.bluedragon.distribution.spotcheck.exceptions;

/**
 * 抽检业务异常
 *
 * @author hujiping
 * @date 2021/8/19 5:02 下午
 */
public class SpotCheckBusinessException extends RuntimeException {
    public SpotCheckBusinessException(String message){
        super(message);
    }
}