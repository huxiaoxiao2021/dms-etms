package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class SendFlowExcepStatisticsReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -779487727963638281L;
    /**
     * 混扫任务编号
     */
    private String templateCode;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }
}
