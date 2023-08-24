package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;
import java.util.List;

public class SendVehicleDto implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;


    /**
     * 车辆状态
     */
    private Integer vehicleStatus;
    /**
     * 车牌号
     */
    private String vehicleNumber;
    /**
     * 发货任务业务主键
     */
    private String sendVehicleBizId;

    private List<SendVehicleDetailDto> sendVehicleDetailDtoList;


    /**
     * 自建任务的流水号
     */
    private String bizNo;
    /**
     * 是否是自建任务 true：自建
     */
    private Boolean manualCreatedFlag;
    /**
     * 自建任务名称
     */
    private String taskName;

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public List<SendVehicleDetailDto> getSendVehicleDetailDtoList() {
        return sendVehicleDetailDtoList;
    }

    public void setSendVehicleDetailDtoList(List<SendVehicleDetailDto> sendVehicleDetailDtoList) {
        this.sendVehicleDetailDtoList = sendVehicleDetailDtoList;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public Boolean getManualCreatedFlag() {
        return manualCreatedFlag;
    }

    public void setManualCreatedFlag(Boolean manualCreatedFlag) {
        this.manualCreatedFlag = manualCreatedFlag;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
