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
}
