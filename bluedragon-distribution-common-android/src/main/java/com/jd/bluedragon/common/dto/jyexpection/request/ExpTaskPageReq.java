package com.jd.bluedragon.common.dto.jyexpection.request;

public class ExpTaskPageReq extends ExpBaseReq {

    private Integer floor;

    // 网格号
    private String gridCode;

    // 状态 JyExpStatusEnum
    private Integer status;

    private Integer pageNumber;

    private Integer pageSize;

    private Integer offSet;

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffSet() {
        return (pageSize - 1) * pageSize;
    }
}
