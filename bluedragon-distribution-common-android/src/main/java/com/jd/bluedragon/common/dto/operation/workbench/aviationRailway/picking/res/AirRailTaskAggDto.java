package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
    private List<Integer> startSiteIdList;
    /**
     * 上游场地名称
     */
    private List<String> startSiteNameList;
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
    /**
     * 待提件数
     */
    private Integer initWaitScanTotalNum;
    /**
     * 交接扫描已提总件数
     */
    private Integer handoverScanTotalNum;

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

    public List<Integer> getStartSiteIdList() {
        return startSiteIdList;
    }

    public void setStartSiteIdList(List<Integer> startSiteIdList) {
        this.startSiteIdList = startSiteIdList;
    }

    public List<String> getStartSiteNameList() {
        return startSiteNameList;
    }

    public void setStartSiteNameList(List<String> startSiteNameList) {
        this.startSiteNameList = startSiteNameList;
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

    public Integer getInitWaitScanTotalNum() {
        return initWaitScanTotalNum;
    }

    public void setInitWaitScanTotalNum(Integer initWaitScanTotalNum) {
        this.initWaitScanTotalNum = initWaitScanTotalNum;
    }

    public Integer getHandoverScanTotalNum() {
        return handoverScanTotalNum;
    }

    public void setHandoverScanTotalNum(Integer handoverScanTotalNum) {
        this.handoverScanTotalNum = handoverScanTotalNum;
    }
}
