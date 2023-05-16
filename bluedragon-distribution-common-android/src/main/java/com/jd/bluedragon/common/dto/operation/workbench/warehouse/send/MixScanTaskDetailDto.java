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
}
