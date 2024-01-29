package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @author liwenji
 * @description
 * @date 2023-05-23 15:38
 */
public class MixScanTaskCompleteReq extends BaseReq implements Serializable {

    /**
     * 混扫任务编号
     */
    private String templateCode;

    /**
     * 混扫任务单个流向业务主键
     */
    private String sendVehicleDetailBizId;

    /**
     * 混扫任务单个流向目的站点ID
     */
    private Long endSiteId;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getSendVehicleDetailBizId() {
        return sendVehicleDetailBizId;
    }

    public void setSendVehicleDetailBizId(String sendVehicleDetailBizId) {
        this.sendVehicleDetailBizId = sendVehicleDetailBizId;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }
}
