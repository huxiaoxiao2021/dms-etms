package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;
import java.util.Date;

public class VehicleDetailTaskDto implements Serializable {
    private static final long serialVersionUID = -846608204511149551L;
    private String bizId;
    private String transWorkItemCode;
    private Integer vehicleStatus;
    private Long  startSiteId;
    private String startSiteName;
    private Long  endSiteId;
    private String endSiteName;
    private Date planDepartTime;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Date getPlanDepartTime() {
        return planDepartTime;
    }

    public void setPlanDepartTime(Date planDepartTime) {
        this.planDepartTime = planDepartTime;
    }
}
