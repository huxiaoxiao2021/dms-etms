package com.jd.bluedragon.distribution.reverse.domain;

/**
 * @program: bluedragon-distribution
 * @description: 逆向入库单类型枚举
 * 1-c2c入备件库
 * @author: liuduo8
 * @create: 2019-12-23 09:05
 **/
public enum ReverseStockInDetailTypeEnum {

    C2C_REVERSE_SPWMS(1,"c2c入备件库");

    private int code;
    private String name;

    public int getCode() {
        return code;
    }
    public String getName() {
        return name;
    }

    ReverseStockInDetailTypeEnum(int code, String name){
        this.code = code;
        this.name = name;
    }
}
