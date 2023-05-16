package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;

public class SendVehicleDetailDto implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;

    /**
     * 状态
     */
    private Integer itemStatus;

    /**
     * 状态描述
     */
    private String itemStatusDesc;

    /**
     * 目的场地
     */
    private Long endSiteId;

    /**
     * 目的场地名称
     */
    private String endSiteName;

    /**
     * 发货明细流向ID
     */
    private String sendDetailBizId;

    /**
     * 主发货任务BizId
     */
    private String bizId;

    /**
     * 滑道-笼车
     */
    private String crossTableTrolley;

    /**
     * 混扫任务进行中
     */
    private Boolean mixScanTaskProcess;

    public String getItemStatusDesc() {
        return itemStatusDesc;
    }

    public void setItemStatusDesc(String itemStatusDesc) {
        this.itemStatusDesc = itemStatusDesc;
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

    public String getSendDetailBizId() {
        return sendDetailBizId;
    }

    public void setSendDetailBizId(String sendDetailBizId) {
        this.sendDetailBizId = sendDetailBizId;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getCrossTableTrolley() {
        return crossTableTrolley;
    }

    public void setCrossTableTrolley(String crossTableTrolley) {
        this.crossTableTrolley = crossTableTrolley;
    }

    public Boolean getMixScanTaskProcess() {
        return mixScanTaskProcess;
    }

    public void setMixScanTaskProcess(Boolean mixScanTaskProcess) {
        this.mixScanTaskProcess = mixScanTaskProcess;
    }

    public Integer getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(Integer itemStatus) {
        this.itemStatus = itemStatus;
    }
}
