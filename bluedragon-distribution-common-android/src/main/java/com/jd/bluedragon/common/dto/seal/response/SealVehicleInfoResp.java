package com.jd.bluedragon.common.dto.seal.response;

import java.io.Serializable;

public class SealVehicleInfoResp implements Serializable {
    private static final long serialVersionUID = 4862292928579823809L;

    /**
     * 运力编码，托盘，重量 体积  车牌号 车型 车型名称
     * */
    private String transportCode;
    private String tray;
    private String weight;
    private Integer volume;
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

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
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
