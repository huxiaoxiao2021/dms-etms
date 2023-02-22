package com.jd.bluedragon.distribution.jy.dto.send;


import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

public class SealCodeReq extends JyReqBaseDto implements Serializable {
    private static final long serialVersionUID = 3469807962560383562L;
    /**
     * 运输任务编码
     */
    private String sendVehicleBizId;
    /**
     * 车牌号
     */
    private String vehicleNumber;



    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }


}
