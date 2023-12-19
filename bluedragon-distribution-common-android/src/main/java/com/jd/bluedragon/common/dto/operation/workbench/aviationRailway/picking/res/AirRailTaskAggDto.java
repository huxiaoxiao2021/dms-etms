package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import java.io.Serializable;
import java.util.Date;

public class AirRailTaskAggDto implements Serializable {
    private static final long serialVersionUID = 3858833954200859405L;
    /**
     * 任务主键
     */
    private String bizId;
    /**
     * 班次号：航班/车次号
     */
    private String serviceNumber;

    /**
     * 上游场地Id
     */
    private Integer startSiteId;
    /**
     * 上游场地名称
     */
    private String startSiteName;
    /**
     * 预计到达时间
     */
    private Date nodePlanArriveTime;
    /**
     * 实际到达时间
     */
    private Date nodeRealArriveTime;
    /**
     * 提货时间
     */
    private Date pickingTime;
    /**
     * 待提件数
     */
    private Integer waitScanTotal;
    /**
     * 已提件数
     */
    private Integer haveScannedTotal;
    /**
     * 多提件数
     */
    private Integer multipleScanTotal;
    /**
     * 是否无任务
     */
    private Boolean noTaskFlag;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Date getNodePlanArriveTime() {
        return nodePlanArriveTime;
    }

    public void setNodePlanArriveTime(Date nodePlanArriveTime) {
        this.nodePlanArriveTime = nodePlanArriveTime;
    }

    public Date getNodeRealArriveTime() {
        return nodeRealArriveTime;
    }

    public void setNodeRealArriveTime(Date nodeRealArriveTime) {
        this.nodeRealArriveTime = nodeRealArriveTime;
    }

    public Date getPickingTime() {
        return pickingTime;
    }

    public void setPickingTime(Date pickingTime) {
        this.pickingTime = pickingTime;
    }

    public Integer getWaitScanTotal() {
        return waitScanTotal;
    }

    public void setWaitScanTotal(Integer waitScanTotal) {
        this.waitScanTotal = waitScanTotal;
    }

    public Integer getHaveScannedTotal() {
        return haveScannedTotal;
    }

    public void setHaveScannedTotal(Integer haveScannedTotal) {
        this.haveScannedTotal = haveScannedTotal;
    }

    public Integer getMultipleScanTotal() {
        return multipleScanTotal;
    }

    public void setMultipleScanTotal(Integer multipleScanTotal) {
        this.multipleScanTotal = multipleScanTotal;
    }

    public Boolean getNoTaskFlag() {
        return noTaskFlag;
    }

    public void setNoTaskFlag(Boolean noTaskFlag) {
        this.noTaskFlag = noTaskFlag;
    }
}
