package com.jd.bluedragon.common.dto.jyexpection.request;

public class ExpTaskPageReq extends ExpBaseReq {

    private Integer floor;

    // 网格号
    private String gridCode;

    // 处理状态 processing_status 0:待录入 1：匹配中    2：上架     3: 处理完成     4：待打印 、   5：审批中、  6、审批通过  、7：审批驳回、8：客服介入
    private Integer processingStatus;

    // 处理人erp
    private String handlerErp;

    // 状态 JyExpStatusEnum
    private Integer status;

    private Integer pageNumber;

    private Integer pageSize;

    private Integer offSet;

    private String gridRid;

    /**
     * 获取异常完结数据范围限制天数
     */
    private Integer limitDay;

    public String getGridRid() {
        return gridRid;
    }

    public void setGridRid(String gridRid) {
        this.gridRid = gridRid;
    }

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

    public Integer getLimitDay() {
        return limitDay;
    }

    public void setLimitDay(Integer limitDay) {
        this.limitDay = limitDay;
    }
}
