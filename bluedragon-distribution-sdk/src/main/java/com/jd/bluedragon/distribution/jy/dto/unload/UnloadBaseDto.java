package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.distribution.jy.dto.CurrentOperate;
import com.jd.bluedragon.distribution.jy.dto.User;

import java.io.Serializable;

public class UnloadBaseDto implements Serializable {

    private static final long serialVersionUID = 2419641078080000602L;
    private CurrentOperate currentOperate;
    private User user;
    private String vehicleNumber;

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
