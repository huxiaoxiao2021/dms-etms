package com.jd.bluedragon.distribution.reverse.domain;

/**
 * @program: bluedragon-distribution
 * @description: 逆向入库单状态枚举
 * 1-发货 2-取消 3-收货 4-驳回 5-创建成功 6-创建失败
 * @author: liuduo8
 * @create: 2019-12-23 09:05
 **/
public enum ReverseStockInDetailStatusEnum {

    SEND(1,"发货"),
    CANCEL(2,"取消"),
    REVERSE(3,"收货"),
    REJECT(4,"驳回"),
    SUCCESS(5,"创建成功"),
    ERROR(6,"创建失败");

    private Integer code;
    private String name;

    public Integer getCode() {
        return code;
    }
    public String getName() {
        return name;
    }

    ReverseStockInDetailStatusEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }
}
