package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestDetail;

import java.util.List;

/**
 * @author liwenji
 * @description 
 * @date 2023-05-22 15:48
 */
public class CarToSealList {

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 主任务ID
     */
    private String sendVehicleBizId;
    
    /**
     * 流向详情
     */
    private List<ToSealDestDetail> sealDestDetails;
    
    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public List<ToSealDestDetail> getSealDestDetails() {
        return sealDestDetails;
    }

    public void setSealDestDetails(List<ToSealDestDetail> sealDestDetails) {
        this.sealDestDetails = sealDestDetails;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }
}
