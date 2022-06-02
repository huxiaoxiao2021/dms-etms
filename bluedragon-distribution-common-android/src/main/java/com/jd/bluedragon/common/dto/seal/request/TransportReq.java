package com.jd.bluedragon.common.dto.seal.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class TransportReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 7633234736944916205L;
    /**
     * 运力编码
     */
    private String transportCode;
    /**
     * 车牌号
     */
    private String vehicleNumber;

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
}
