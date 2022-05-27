package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class VehicleTaskReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -381204869459707754L;
    /**
     * 始发场地
     */
    private Long startSiteId;
    /**
     * 目的场地ID
     */
    private Long endSiteId;
    /**
     * 任务状态；0-待发货，1-发货中，2-待封车，3-已封车
     */
    private Integer vehicleStatus;

    /**
     * 包裹号
     */
    private String packageCode;

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
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
}
