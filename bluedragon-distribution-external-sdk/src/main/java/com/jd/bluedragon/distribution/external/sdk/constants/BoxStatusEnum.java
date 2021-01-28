package com.jd.bluedragon.distribution.external.sdk.constants;

public enum BoxStatusEnum {
    OPEN(1,"可以使用"),
    CLOSE(-1,"不可使用");

    private Integer status;
    private String statusName;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    BoxStatusEnum(Integer status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }
}
