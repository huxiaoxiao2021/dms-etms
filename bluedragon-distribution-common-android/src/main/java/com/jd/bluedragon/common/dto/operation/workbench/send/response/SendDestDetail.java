package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName SendDestDetail
 * @Description
 * @Author wyh
 * @Date 2022/5/19 16:54
 **/
public class SendDestDetail implements Serializable {

    private static final long serialVersionUID = 3687255270847724701L;

    /**
     * 发货目的地
     */
    private Integer endSiteId;

    /**
     * 发货目的地
     */
    private String endSiteName;

    /**
     * 待扫包裹数
     */
    private Long toScanPackCount = 0L;

    /**
     * 已扫包裹数
     */
    private Long scannedPackCount = 0L;

    /**
     * 预计发货时间
     */
    private Date planDepartTime;

    /**
     * 任务状态；0-待发货，1-发货中，2-待封车，3-已封车，4-已作废
     */
    private Integer vehicleStatus;
    
    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Long getToScanPackCount() {
        return toScanPackCount;
    }

    public void setToScanPackCount(Long toScanPackCount) {
        this.toScanPackCount = toScanPackCount;
    }

    public Long getScannedPackCount() {
        return scannedPackCount;
    }

    public void setScannedPackCount(Long scannedPackCount) {
        this.scannedPackCount = scannedPackCount;
    }

    public Date getPlanDepartTime() {
        return planDepartTime;
    }

    public void setPlanDepartTime(Date planDepartTime) {
        this.planDepartTime = planDepartTime;
    }

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }
}
