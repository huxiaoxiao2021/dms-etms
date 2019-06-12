package com.jd.bluedragon.distribution.inventory.domain;

/**
 * 盘点任务状态枚举
 */
public enum InventoryTaskStatusEnum {

    DOING(0, "进行中"),
    DONE(1, "已结束"),
    CANCEL(2, "已取消");

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
