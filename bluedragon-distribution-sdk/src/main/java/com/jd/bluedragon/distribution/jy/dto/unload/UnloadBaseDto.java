package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.distribution.jy.dto.CurrentOperate;
import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;
import com.jd.bluedragon.distribution.jy.dto.User;

import java.io.Serializable;

public class UnloadBaseDto extends JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = 2419641078080000602L;

    private String vehicleNumber;


    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
