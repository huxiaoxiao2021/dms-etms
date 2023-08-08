package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 18:18
 * @Description
 */
public class ShuttleTaskSealCarQueryRes implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private String bizId;

    private String vehicleNumber;

    private Double weight;

    private Double volume;
    /**
     * 件数
     */
    private Integer itemNum;
    /**
     * 运力编码
     */
    private String transportCode;
    /**
     * 出发时间
     */
    private String departureTimeStr;
    /**
     * 已封航空任务数量
     */
    private Integer taskNum;
    /**
     * 封签码数量
     */
    private Integer sealCodeNum;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
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

    public Integer getItemNum() {
        return itemNum;
    }

    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public Integer getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(Integer taskNum) {
        this.taskNum = taskNum;
    }

    public Integer getSealCodeNum() {
        return sealCodeNum;
    }

    public void setSealCodeNum(Integer sealCodeNum) {
        this.sealCodeNum = sealCodeNum;
    }

    public String getDepartureTimeStr() {
        return departureTimeStr;
    }

    public void setDepartureTimeStr(String departureTimeStr) {
        this.departureTimeStr = departureTimeStr;
    }
}
