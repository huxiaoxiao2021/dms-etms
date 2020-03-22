package com.jd.bluedragon.distribution.coldchain.dto;

/**
 * @author lixin39
 * @Description 冷链卸货任务查询结果
 * @ClassName ColdChainUnloadQueryResultDto
 * @date 2020/3/4
 */
public class ColdChainUnloadQueryResultDto {

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 卸货时间
     */
    private String unloadTime;

    /**
     * 车牌号
     */
    private String vehicleNo;

    /**
     * 车型名称
     */
    private String vehicleModelName;

    /**
     * 剩余时间
     */
    private String remainingTime;

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public String getUnloadTime() {
        return unloadTime;
    }

    public void setUnloadTime(String unloadTime) {
        this.unloadTime = unloadTime;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getVehicleModelName() {
        return vehicleModelName;
    }

    public void setVehicleModelName(String vehicleModelName) {
        this.vehicleModelName = vehicleModelName;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }
}
