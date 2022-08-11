package com.jd.bluedragon.common.dto.jyexpection.request;

public class ExpTaskPageReq extends ExpBaseReq {
    // 网格号
    private String gridNo;

    // 状态 JyExpStatusEnum
    private String status;

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
