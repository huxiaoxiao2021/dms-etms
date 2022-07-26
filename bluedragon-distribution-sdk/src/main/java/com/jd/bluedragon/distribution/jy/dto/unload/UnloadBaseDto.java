package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.distribution.jy.dto.CurrentOperate;
import com.jd.bluedragon.distribution.jy.dto.User;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class UnloadBaseDto implements Serializable {

    private static final long serialVersionUID = 2419641078080000602L;
    @NotNull(message = "操作人单位信息不能为空")
    private CurrentOperate currentOperate;
    @NotNull(message = "操作人信息不能为空")
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
