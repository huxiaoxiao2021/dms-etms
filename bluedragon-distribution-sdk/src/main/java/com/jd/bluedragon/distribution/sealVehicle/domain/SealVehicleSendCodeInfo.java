package com.jd.bluedragon.distribution.sealVehicle.domain;

import java.util.List;

public class SealVehicleSendCodeInfo {

    private static final long serialVersionUID = 1L;

    /*
    * 批次
    * */
    private String sendCode;

    /*
    * 车牌
    * */
    private String vehicleNumber;

    /*
    * 批次信息是否就绪
    * */
    private Boolean isReady;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Boolean getReady() {
        return isReady;
    }

    public void setReady(Boolean ready) {
        isReady = ready;
    }

}
