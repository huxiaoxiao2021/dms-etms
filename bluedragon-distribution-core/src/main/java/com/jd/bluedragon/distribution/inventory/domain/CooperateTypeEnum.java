package com.jd.bluedragon.distribution.inventory.domain;

/**
 * 盘点任务协助类型枚举
 */
public enum CooperateTypeEnum {
    OWNER(1, "创建"),
    COOPERATOR(2, "协助");

    private Integer code;

    private String desc;

    CooperateTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
