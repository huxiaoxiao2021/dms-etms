package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @author liwenji
 * @date 2023-05-16 14:11
 */
public class MixScanTaskFlowDetailReq extends BaseReq implements Serializable {

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
