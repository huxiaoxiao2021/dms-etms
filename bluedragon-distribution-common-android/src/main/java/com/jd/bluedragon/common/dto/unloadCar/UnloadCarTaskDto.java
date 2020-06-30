package com.jd.bluedragon.common.dto.unloadCar;

import java.io.Serializable;

/**
 * @ClassName: UnloadCarTaskDto
 * @Description: 任务对象
 * @Author: biyubo
 * @CreateDate: 2020-6-21 20:32
 */

public class UnloadCarTaskDto implements Serializable {
    private static final long serialVersionUID = -1L;

    private Integer serialNumber;
    private String taskCode;
    private String carCode;
    private String platformName;
    private String batchCode;
    private Integer batchNum;
    private Integer waybillNum;
    private Integer packageNum;
    private Integer taskStatus;
    private String taskStatusName;

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Integer getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Integer batchNum) {
        this.batchNum = batchNum;
    }

    public Integer getWaybillNum() {
        return waybillNum;
    }

    public void setWaybillNum(Integer waybillNum) {
        this.waybillNum = waybillNum;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskStatusName() {
        return taskStatusName;
    }

    public void setTaskStatusName(String taskStatusName) {
        this.taskStatusName = taskStatusName;
    }
}