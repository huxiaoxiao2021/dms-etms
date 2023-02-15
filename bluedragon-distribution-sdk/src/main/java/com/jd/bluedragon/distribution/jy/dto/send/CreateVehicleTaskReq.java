package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

public class CreateVehicleTaskReq extends JyReqBaseDto implements Serializable {
    private static final long serialVersionUID = -5531009617431175788L;

    /**
     * 车型
     */
    private Integer vehicleType;
    /**
     * 车型名称
     */
    private String vehicleTypeName;

    public Integer getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Integer vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleTypeName() {
        return vehicleTypeName;
    }

    public void setVehicleTypeName(String vehicleTypeName) {
        this.vehicleTypeName = vehicleTypeName;
    }
}
