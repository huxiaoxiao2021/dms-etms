package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import java.io.Serializable;

public class TransferVehicleTaskReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 420640648074010581L;
    /**
     * 迁入迁出标识
     * 1 source（出） 2 target （入）
     */
    private Integer transferFlag;
    /**
     * 包裹号
     */
    private String packageCode;
    /**
     * 目的场地ID
     */
    private Long endSiteId;
    /**
     * 任务状态；0-待发货，1-发货中，2-待封车，3-已封车
     */
    private Integer vehicleStatus;

    private Integer pageNumber;

    private Integer pageSize;

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

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
    public Integer getTransferFlag() { return transferFlag; }

    public void setTransferFlag(Integer transferFlag) { this.transferFlag = transferFlag; }
}
