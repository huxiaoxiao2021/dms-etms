package com.jd.bluedragon.distribution.spotcheck.exceptions;

/**
 * 抽检系统异常
 *
 * @author hujiping
 * @date 2021/8/19 5:02 下午
 */
public class SpotCheckSysException extends RuntimeException {
    public SpotCheckSysException(String message){
        super(message);
    }
}