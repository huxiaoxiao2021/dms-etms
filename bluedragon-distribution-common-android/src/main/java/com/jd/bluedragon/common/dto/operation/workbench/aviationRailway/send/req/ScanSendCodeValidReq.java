package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class ScanSendCodeValidReq extends BaseReq implements Serializable {


    private static final long serialVersionUID = 8030587778588887945L;

    /**
     * 运力编码
     * */
    private String transportCode;
    /**
     * 车牌号
     * */
    private String vehicleNumber;
    /**
     * 批次号
     */
    private String sendCode;

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }
}
