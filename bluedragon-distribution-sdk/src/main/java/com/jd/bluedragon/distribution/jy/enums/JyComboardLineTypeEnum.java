package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName JySendLineTypeEnum
 * @Description 拣运发货岗任务线路类型
 * @Author wyh
 * @Date 2022/5/29 14:15
 **/
public enum JyComboardLineTypeEnum {

    TRANSFER(JyLineTypeEnum.TRANSFER.getCode(), "传", 1);

    private Integer code;
    private String name;
    private Integer order;

    JyComboardLineTypeEnum(Integer code, String name, Integer order) {
        this.code = code;
        this.name = name;
        this.order = order;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public Integer getOrder() {
        return order;
    }
}
