package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

public class BuQiCancelSendScanReq extends BaseReq implements Serializable {


    private static final long serialVersionUID = -5809332610524693231L;


    /**
     * 混扫任务编码
     */
    private String mixScanTaskCode;

    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;

    /**
     * 包裹号列表
     */
    private List<String> packList;

    /**
     * 是否全选
     */
    private Boolean checkAllFlag;
    /**
     * 运单号列表
     */
    private List<String> waybillCodeList;


    public String getMixScanTaskCode() {
        return mixScanTaskCode;
    }

    public void setMixScanTaskCode(String mixScanTaskCode) {
        this.mixScanTaskCode = mixScanTaskCode;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public List<String> getPackList() {
        return packList;
    }

    public void setPackList(List<String> packList) {
        this.packList = packList;
    }

    public Boolean getCheckAllFlag() {
        return checkAllFlag;
    }

    public void setCheckAllFlag(Boolean checkAllFlag) {
        this.checkAllFlag = checkAllFlag;
    }

    public List<String> getWaybillCodeList() {
        return waybillCodeList;
    }

    public void setWaybillCodeList(List<String> waybillCodeList) {
        this.waybillCodeList = waybillCodeList;
    }
}
