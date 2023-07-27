package com.jd.bluedragon.distribution.jy.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/11 15:14
 * @Description:
 */
public enum JySendPredictTypeEnum {

    CURRENT_WAVE(1,"当前波次"),
    PREVIOUS_WAVE(2,"上个波次");


    JySendPredictTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private Integer code;

    private String name;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
