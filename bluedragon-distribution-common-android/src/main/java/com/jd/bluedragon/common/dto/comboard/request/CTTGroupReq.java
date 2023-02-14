package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class CTTGroupReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -7463236650303228086L;
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
