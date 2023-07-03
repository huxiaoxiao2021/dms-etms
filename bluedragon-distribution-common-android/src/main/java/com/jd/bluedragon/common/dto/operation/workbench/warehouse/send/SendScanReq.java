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

    /**
     * 操作类型（2种）
     * com.jd.bluedragon.common.dto.operation.workbench.enums.JySendFlowConfigEnum
     */
    private Integer operateType;

    /**
     * 1、非关注流向强发
     * 2、未添加流向强发也走该字段
     */
    private Boolean unfocusedFlowForceSend;

    /**
     * 上一次扫描的单号
     */
    private String lastBarCode;
    /**
     * 上一次扫描的单号
     */
    private String lastNextSiteCode;
    /**
     * 上一次扫描的发货明细任务bizId
     */
    private String lastDetailBizId;


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

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }


    public Boolean getUnfocusedFlowForceSend() {
        return unfocusedFlowForceSend;
    }

    public void setUnfocusedFlowForceSend(Boolean unfocusedFlowForceSend) {
        this.unfocusedFlowForceSend = unfocusedFlowForceSend;
    }

    public String getLastBarCode() {
        return lastBarCode;
    }

    public void setLastBarCode(String lastBarCode) {
        this.lastBarCode = lastBarCode;
    }

    public String getLastNextSiteCode() {
        return lastNextSiteCode;
    }

    public void setLastNextSiteCode(String lastNextSiteCode) {
        this.lastNextSiteCode = lastNextSiteCode;
    }

    public String getLastDetailBizId() {
        return lastDetailBizId;
    }

    public void setLastDetailBizId(String lastDetailBizId) {
        this.lastDetailBizId = lastDetailBizId;
    }
}
