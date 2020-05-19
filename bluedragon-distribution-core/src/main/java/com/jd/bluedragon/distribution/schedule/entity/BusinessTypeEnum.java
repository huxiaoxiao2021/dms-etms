package com.jd.bluedragon.distribution.schedule.entity;

/**
 * 调度业务类型
 * @author wuyoude
 *
 */
public enum BusinessTypeEnum {

    EDN(1, "企配仓业务");

    private Integer code;

    private String name;

    BusinessTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

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
