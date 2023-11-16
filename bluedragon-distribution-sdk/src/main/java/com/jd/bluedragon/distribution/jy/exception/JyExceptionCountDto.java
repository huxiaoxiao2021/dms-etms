package com.jd.bluedragon.distribution.jy.exception;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/16 16:29
 * @Description:
 */
public class JyExceptionCountDto {

    private String exceptionName;

    private Integer count;

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
