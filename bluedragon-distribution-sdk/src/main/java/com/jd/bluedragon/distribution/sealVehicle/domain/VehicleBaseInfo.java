package com.jd.bluedragon.distribution.sealVehicle.domain;

public class VehicleBaseInfo {

    private static final long serialVersionUID = 1L;

    /** 车牌号 */
    private String vehicleNumber;

    /** 封签号 */
    private String sealCode;

    /** 重量 */
    private Double weight;

    /** 体积 */
    private Double volume;

    /*
    * 车牌信息是否就绪
    * */
    private Boolean isReady;

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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Boolean getReady() {
        return isReady;
    }

    public void setReady(Boolean ready) {
        isReady = ready;
    }
}
