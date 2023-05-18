package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @author liwenji
 * @description 
 * @date 2023-05-16 16:29
 */
public class RemoveMixScanTaskFlowReq extends BaseReq implements Serializable {

    /**
     * 混扫任务编号
     */
    private String templateCode;

    /**
     * 发货明细流向ID
     */
    private String sendVehicleDetailBizId;

    /**
     * 目的地站点
     */
    private Integer endSiteId;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }
    

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getSendVehicleDetailBizId() {
        return sendVehicleDetailBizId;
    }

    public void setSendVehicleDetailBizId(String sendVehicleDetailBizId) {
        this.sendVehicleDetailBizId = sendVehicleDetailBizId;
    }
}
