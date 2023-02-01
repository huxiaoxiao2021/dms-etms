package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class VehicleTaskDto implements Serializable {
    private static final long serialVersionUID = -4655590197698641999L;
    /**
     * 主任务相关信息
     */
    private String bizId;
    private String transWorkCode;
    private String vehicleNumber;
    private Integer vehicleStatus;
    private Integer transWay;
    private String transWayName;
    private Integer vehicleType;
    private String vehicleTypeName;
    private Integer lineType;
    private String lineTypeName;

    /**
     * 装载率 百分制
     */
    private BigDecimal loadRate;
    /**
     * 子任务列表
     */
    List<VehicleDetailTaskDto> vehicleDetailTaskDtoList;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getTransWorkCode() {
        return transWorkCode;
    }

    public void setTransWorkCode(String transWorkCode) {
        this.transWorkCode = transWorkCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Integer getTransWay() {
        return transWay;
    }

    public void setTransWay(Integer transWay) {
        this.transWay = transWay;
    }

    public String getTransWayName() {
        return transWayName;
    }

    public void setTransWayName(String transWayName) {
        this.transWayName = transWayName;
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

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }

    public String getLineTypeName() {
        return lineTypeName;
    }

    public void setLineTypeName(String lineTypeName) {
        this.lineTypeName = lineTypeName;
    }

    public List<VehicleDetailTaskDto> getVehicleDetailTaskDtoList() {
        return vehicleDetailTaskDtoList;
    }

    public void setVehicleDetailTaskDtoList(List<VehicleDetailTaskDto> vehicleDetailTaskDtoList) {
        this.vehicleDetailTaskDtoList = vehicleDetailTaskDtoList;
    }

    public BigDecimal getLoadRate() {
        return loadRate;
    }

    public void setLoadRate(BigDecimal loadRate) {
        this.loadRate = loadRate;
    }
}
