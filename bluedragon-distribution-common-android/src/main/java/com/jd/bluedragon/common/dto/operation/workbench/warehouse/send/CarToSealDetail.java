package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestDetail;

/**
 * @author liwenji
 * @description 
 * @date 2023-05-22 15:48
 */
public class CarToSealDetail extends ToSealDestDetail {

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 主任务ID
     */
    private String sendVehicleBizId;
    
    
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
    
}
