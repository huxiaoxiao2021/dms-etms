package com.jd.bluedragon.distribution.newseal.domain;

/**
 * 封车执行结果枚举
 */
public enum SealVehicleExecute {
    SUCCESS(1,"封车成功"),
    FAIL(2,"封车失败"),
    REMOVE_EMPTY_BATCH(3,"封车去除空批次");

    private Integer code;
    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    SealVehicleExecute(Integer code, String name){
        this.code = code;
        this.name = name;
    }
}
