package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;

public class MixScanTaskDetailDto implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;

    /**
     * 混扫任务信息
     */
    private String templateCode;
    private String templateName;
    /**
     * 滑道
     */
    private String crossCode;
    /**
     * 笼车号
     */
    private String tabletrolleyCode;
    /**
     * 开始场地
     */
    private Long startSiteId;
    private String startSiteName;
    /**
     * 下一场地
     */
    private Long endSiteId;
    private String endSiteName;
    /**
     * 发货明细流向ID
     */
    private String sendVehicleDetailBizId;
    /**
     * 是否关注
     */
    private Integer focus;
    
    private String vehicleNumber;

    /**
     * 是否是自建任务 true：自建
     */
    private Boolean manualCreatedFlag;

    /**
     * 主任务ID
     */
    private String sendVehicleBizId;

    /**
     * 自建任务名称
     */
    private String taskName;

    /**
     * 自建任务的流水号
     */
    private String bizNo;
    
    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCrossCode() {
        return crossCode;
    }

    public void setCrossCode(String crossCode) {
        this.crossCode = crossCode;
    }

    public String getTabletrolleyCode() {
        return tabletrolleyCode;
    }

    public void setTabletrolleyCode(String tabletrolleyCode) {
        this.tabletrolleyCode = tabletrolleyCode;
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

    public String getSendVehicleDetailBizId() {
        return sendVehicleDetailBizId;
    }

    public void setSendVehicleDetailBizId(String sendVehicleDetailBizId) {
        this.sendVehicleDetailBizId = sendVehicleDetailBizId;
    }

    public Integer getFocus() {
        return focus;
    }

    public void setFocus(Integer focus) {
        this.focus = focus;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Boolean getManualCreatedFlag() {
        return manualCreatedFlag;
    }

    public void setManualCreatedFlag(Boolean manualCreatedFlag) {
        this.manualCreatedFlag = manualCreatedFlag;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }
}
