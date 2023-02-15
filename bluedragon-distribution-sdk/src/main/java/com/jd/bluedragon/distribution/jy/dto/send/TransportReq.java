package com.jd.bluedragon.distribution.jy.dto.send;


import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

public class TransportReq extends JyReqBaseDto implements Serializable {
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
