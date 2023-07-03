package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;


public class SealCarCheckDtoReq extends BaseReq implements Serializable {
    /**
     * 混扫任务编号
     */
    private String mixScanTaskCode;

    public String getMixScanTaskCode() {
        return mixScanTaskCode;
    }

    public void setMixScanTaskCode(String mixScanTaskCode) {
        this.mixScanTaskCode = mixScanTaskCode;
    }
}
