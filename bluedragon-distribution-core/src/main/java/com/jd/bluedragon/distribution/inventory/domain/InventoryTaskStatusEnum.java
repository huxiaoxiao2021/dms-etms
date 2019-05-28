package com.jd.bluedragon.distribution.inventory.domain;

/**
 * 盘点任务状态枚举
 */
public enum InventoryTaskStatusEnum {

    DOING(1, "进行中"),
    DONE(2, "已结束");

    private Integer statusCode;

    private String statusDesc;

    InventoryTaskStatusEnum(Integer statusCode, String statusDesc) {
        this.statusCode = statusCode;
        this.statusDesc = statusDesc;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }
}
