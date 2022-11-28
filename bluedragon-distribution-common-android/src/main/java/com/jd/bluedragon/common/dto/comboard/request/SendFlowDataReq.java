package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class SendFlowDataReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 8046944132685205767L;
    /**
     * 混扫任务编号
     */
    private String templateCode;

    /**
     * 目的id
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
}
