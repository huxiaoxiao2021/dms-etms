package com.jd.bluedragon.common.dto.operation.workbench.enums;

public enum CalibrateDetailStatusEnum {

    PENDING(0, "待处理"),
    SOLVED(1, "已完成"),
    TIMEOUT(2, "超时");
    private Integer statusCode;
    private String statusName;
    CalibrateDetailStatusEnum(Integer statusCode, String statusName){
        this.statusCode = statusCode;
        this.statusName = statusName;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
