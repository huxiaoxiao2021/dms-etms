package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class VehicleTypeDto implements Serializable {

    private static final long serialVersionUID = -3930599933845221946L;
    private Integer vehicleType;//车型
    private String vehicleTypeName;//车型名称
    private Double volume;//标准容积(单位:立方米)
    private Double weight;//核定载重(单位:吨)
    private String vehicleLength;//车长（单位：厘米）
    private String vehicleLengthName;
    private String vehicleKind;//车辆种类
    private String vehicleKindName;
    private Integer minTray;//托盘最小量（托）
    private Integer maxTray;//托盘理论满载值（托
    private String vehicleWidth;
    private String vehicleHeight;



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

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getVehicleLength() {
        return vehicleLength;
    }

    public void setVehicleLength(String vehicleLength) {
        this.vehicleLength = vehicleLength;
    }

    public String getVehicleLengthName() {
        return vehicleLengthName;
    }

    public void setVehicleLengthName(String vehicleLengthName) {
        this.vehicleLengthName = vehicleLengthName;
    }

    public String getVehicleKind() {
        return vehicleKind;
    }

    public void setVehicleKind(String vehicleKind) {
        this.vehicleKind = vehicleKind;
    }

    public String getVehicleKindName() {
        return vehicleKindName;
    }

    public void setVehicleKindName(String vehicleKindName) {
        this.vehicleKindName = vehicleKindName;
    }

    public Integer getMinTray() {
        return minTray;
    }

    public void setMinTray(Integer minTray) {
        this.minTray = minTray;
    }

    public Integer getMaxTray() {
        return maxTray;
    }

    public void setMaxTray(Integer maxTray) {
        this.maxTray = maxTray;
    }

    public String getVehicleWidth() {
        return vehicleWidth;
    }

    public void setVehicleWidth(String vehicleWidth) {
        this.vehicleWidth = vehicleWidth;
    }

    public String getVehicleHeight() {
        return vehicleHeight;
    }

    public void setVehicleHeight(String vehicleHeight) {
        this.vehicleHeight = vehicleHeight;
    }
}
