package com.jd.bluedragon.common.dto.jyexpection.request;

public class ExpTaskPageReq extends ExpBaseReq {

    private Integer floor;

    // 网格号
    private String gridCode;

    // 处理状态0:待录入 1：待匹配 2：暂存 3: 处理完成
    private Integer processingStatus;

    // 处理人erp
    private String handlerErp;

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
        if (pageNumber == null || pageSize == null) {
            return 0;
        }
        return (pageNumber - 1) * pageSize;
    }

    public void setOffSet(Integer offSet) {
        if (pageNumber == null || pageSize == null) {
            this.offSet = 0;
        }else {
            this.offSet = (pageNumber - 1) * pageSize;
        }
    }

    public Integer getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(Integer processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getHandlerErp() {
        return handlerErp;
    }

    public void setHandlerErp(String handlerErp) {
        this.handlerErp = handlerErp;
    }
}
