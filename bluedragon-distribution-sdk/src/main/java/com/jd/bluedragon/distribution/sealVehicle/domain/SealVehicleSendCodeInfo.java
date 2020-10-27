package com.jd.bluedragon.distribution.sealVehicle.domain;

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

    /**
     * 封签号
     */
    private String sealCode;

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

    public String getSealCode() {
        return sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }

    public Boolean getReady() {
        return isReady;
    }

    public void setReady(Boolean ready) {
        isReady = ready;
    }

}
