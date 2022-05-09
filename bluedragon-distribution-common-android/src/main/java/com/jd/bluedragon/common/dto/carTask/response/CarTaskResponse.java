package com.jd.bluedragon.common.dto.carTask.response;

import java.io.Serializable;
import java.util.Date;

public class CarTaskResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private String routeLineCode;

    private String endNodeCode;

    private String endNodeName;

    private Double sumVolume;

    private Double volume;

    private Double predictedDeviation;

    private Double adjustVolume;

    private String vehicleTypeCount;

    private int vehicleChangeFlag;

    private Date planDepartTime;

    private int packageCount;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRouteLineCode() {
        return routeLineCode;
    }

    public void setRouteLineCode(String routeLineCode) {
        this.routeLineCode = routeLineCode;
    }

    public String getEndNodeCode() {
        return endNodeCode;
    }

    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public String getEndNodeName() {
        return endNodeName;
    }

    public void setEndNodeName(String endNodeName) {
        this.endNodeName = endNodeName;
    }

    public Double getSumVolume() {
        return sumVolume;
    }

    public void setSumVolume(Double sumVolume) {
        this.sumVolume = sumVolume;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getPredictedDeviation() {
        return predictedDeviation;
    }

    public void setPredictedDeviation(Double predictedDeviation) {
        this.predictedDeviation = predictedDeviation;
    }

    public Double getAdjustVolume() {
        return adjustVolume;
    }

    public void setAdjustVolume(Double adjustVolume) {
        this.adjustVolume = adjustVolume;
    }

    public String getVehicleTypeCount() {
        return vehicleTypeCount;
    }

    public void setVehicleTypeCount(String vehicleTypeCount) {
        this.vehicleTypeCount = vehicleTypeCount;
    }

    public int getVehicleChangeFlag() {
        return vehicleChangeFlag;
    }

    public void setVehicleChangeFlag(int vehicleChangeFlag) {
        this.vehicleChangeFlag = vehicleChangeFlag;
    }

    public Date getPlanDepartTime() {
        return planDepartTime;
    }

    public void setPlanDepartTime(Date planDepartTime) {
        this.planDepartTime = planDepartTime;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }
}
