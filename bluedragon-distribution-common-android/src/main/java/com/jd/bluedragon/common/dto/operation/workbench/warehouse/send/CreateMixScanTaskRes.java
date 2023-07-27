package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;

/**
 * @author liwenji
 * @description 
 * @date 2023-05-18 11:18
 */
public class CreateMixScanTaskRes implements Serializable {
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
