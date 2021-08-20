package com.jd.bluedragon.distribution.spotcheck.exceptions;

/**
 * 自定义抽检异常
 *
 * @author hujiping
 * @date 2021/8/19 5:02 下午
 */
public class SpotCheckException extends RuntimeException {
    public SpotCheckException(String message){
        super(message);
    }
}