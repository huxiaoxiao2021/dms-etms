package com.jd.bluedragon.distribution.jy.dto.send;



import com.jd.bluedragon.distribution.jy.dto.JyVehicleStatusStatis;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SendVehicleTaskResponse
 * @Description
 * @Author wyh
 * @Date 2022/5/17 20:51
 **/
public class SendVehicleTaskResp implements Serializable {

    private static final long serialVersionUID = -4406632213133779677L;

    /**
     * 车辆状态数量统计
     */
    private List<JyVehicleStatusStatis> statusAgg;

    /**
     * 待发货任务
     */
    private SendVehicleData<ToSendVehicle> tysToSendVehicleData;

    /**
     * 发货中任务
     */
    private SendVehicleData<SendingVehicle> sendingVehicleData;

    /**
     * 待封车任务
     */
    private SendVehicleData<ToSealVehicle> toSealVehicleData;

    /**
     * 封车完成任务
     */
    private SendVehicleData<SealedVehicle> sealedVehicleData;

    public List<JyVehicleStatusStatis> getStatusAgg() {
        return statusAgg;
    }

    public void setStatusAgg(List<JyVehicleStatusStatis> statusAgg) {
        this.statusAgg = statusAgg;
    }

    public SendVehicleData<ToSendVehicle> getToSendVehicleData() {
        return tysToSendVehicleData;
    }

    public void setToSendVehicleData(SendVehicleData<ToSendVehicle> tysToSendVehicleData) {
        this.tysToSendVehicleData = tysToSendVehicleData;
    }

    public SendVehicleData<SendingVehicle> getSendingVehicleData() {
        return sendingVehicleData;
    }

    public void setSendingVehicleData(SendVehicleData<SendingVehicle> sendingVehicleData) {
        this.sendingVehicleData = sendingVehicleData;
    }

    public SendVehicleData<ToSealVehicle> getToSealVehicleData() {
        return toSealVehicleData;
    }

    public void setToSealVehicleData(SendVehicleData<ToSealVehicle> toSealVehicleData) {
        this.toSealVehicleData = toSealVehicleData;
    }

    public SendVehicleData<SealedVehicle> getSealedVehicleData() {
        return sealedVehicleData;
    }

    public void setSealedVehicleData(SendVehicleData<SealedVehicle> sealedVehicleData) {
        this.sealedVehicleData = sealedVehicleData;
    }
}
