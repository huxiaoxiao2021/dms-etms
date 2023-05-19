package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendScanRequest;

public class SendScanReq extends SendScanRequest {

    /**
     * 混扫任务编码
     */
    private String mixScanTaskCode;

    /**
     * 设备号
     */
    private String machineCode;


    public String getMixScanTaskCode() {
        return mixScanTaskCode;
    }

    public void setMixScanTaskCode(String mixScanTaskCode) {
        this.mixScanTaskCode = mixScanTaskCode;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }
}
