package com.jd.bluedragon.common.dto.seal.response;

import java.io.Serializable;

public class SealVehicleInfoResp implements Serializable {
    private static final long serialVersionUID = 4862292928579823809L;

    /**
     * 运力编码
     * */
    private String transportCode;
    private String tray;
    /**
     * 已扫总量总计/吨
     */
    private String weight;
    /**
     * 已扫体积总计/立方米
     */
    private String volume;
    private String vehicleNumber;
    private Integer vehicleType;
    private String vehicleTypeName;

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getTray() {
        return tray;
    }

    public void setTray(String tray) {
        this.tray = tray;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

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
