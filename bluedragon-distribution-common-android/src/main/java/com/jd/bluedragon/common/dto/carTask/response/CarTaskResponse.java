package com.jd.bluedragon.common.dto.carTask.response;

import java.io.Serializable;
import java.util.Date;

public class CarTaskResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    /**
     * 路由线路编码
     */
    private String routeLineCode;

    /**
     * 始发网点编码
     */
    private String beginNodeCode;

    /**
     * 始发网点名称
     */
    private String beginNodeName;

    /**
     * 起始网点类型
     */
    private String startNodeType;

    /**
     * 目的网点编码
     */
    private String endNodeCode;

    /**
     * 目的网点名称
     */
    private String endNodeName;

    /**
     * 目的网点类型
     */
    private String endNodeType;

    /**
     * 流向方量
     */
    private Double sumVolume;

    /**
     * 线路方量（弃用）
     */
    private Double volume;

    /**
     * 预测偏差（%）
     */
    private Double predictDeviation;


    /**
     * 车型车数(Map<车型,车数>toJson)
     */
    private String vehicleTypeCount;

    /**
     * 车型车数变更标识：1-变更
     */
    private int vehicleChangeFlag;

    /**
     * 计划发车时间
     */
    private Date planDepartTime;

    /**
     * 包裹数量
     */
    private Integer packageCount;

    /**
     * 调整方量
     */
    private Double adjustVolume;

    /**
     * 线路方量
     */
    private Double algoVolume;


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

    public String getBeginNodeCode() {
        return beginNodeCode;
    }

    public void setBeginNodeCode(String beginNodeCode) {
        this.beginNodeCode = beginNodeCode;
    }

    public String getBeginNodeName() {
        return beginNodeName;
    }

    public void setBeginNodeName(String beginNodeName) {
        this.beginNodeName = beginNodeName;
    }

    public String getStartNodeType() {
        return startNodeType;
    }

    public void setStartNodeType(String startNodeType) {
        this.startNodeType = startNodeType;
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

    public String getEndNodeType() {
        return endNodeType;
    }

    public void setEndNodeType(String endNodeType) {
        this.endNodeType = endNodeType;
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

    public Double getPredictDeviation() {
        return predictDeviation;
    }

    public void setPredictDeviation(Double predictDeviation) {
        this.predictDeviation = predictDeviation;
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

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public Double getAdjustVolume() {
        return adjustVolume;
    }

    public void setAdjustVolume(Double adjustVolume) {
        this.adjustVolume = adjustVolume;
    }

    public Double getAlgoVolume() {
        return algoVolume;
    }

    public void setAlgoVolume(Double algoVolume) {
        this.algoVolume = algoVolume;
    }
}
