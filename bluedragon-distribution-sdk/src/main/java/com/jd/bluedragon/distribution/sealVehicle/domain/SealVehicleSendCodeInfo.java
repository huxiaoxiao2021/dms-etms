package com.jd.bluedragon.distribution.sealVehicle.domain;

public class SealVehicleSendCodeInfo {

    private static final long serialVersionUID = 1L;

    /*
    * 批次
    * */
    private String sendCode;

    /*
    * 车辆基础信息
    * */
    private VehicleBaseInfo vehicleBaseInfo;

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

    public VehicleBaseInfo getVehicleBaseInfo() {
        return vehicleBaseInfo;
    }

    public void setVehicleBaseInfo(VehicleBaseInfo vehicleBaseInfo) {
        this.vehicleBaseInfo = vehicleBaseInfo;
    }

    public Boolean getReady() {
        return isReady;
    }

    public void setReady(Boolean ready) {
        isReady = ready;
    }
}
