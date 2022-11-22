package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

public class BindVehicleDetailTaskReq extends JyReqBaseDto implements Serializable {
    private static final long serialVersionUID = 360399368478325772L;
    /**
     * 迁移前的主任务编号
     */
    private String fromSendVehicleBizId;
    /**
     * 迁移前的子任务编号
     */
    private String fromSendVehicleDetailBizId;
    /**
     * 迁移后的主任务编号
     */
    private String toSendVehicleBizId;
    /**
     * 迁移后的子任务编号
     */
    private String toSendVehicleDetailBizId;

    public String getFromSendVehicleBizId() {
        return fromSendVehicleBizId;
    }

    public void setFromSendVehicleBizId(String fromSendVehicleBizId) {
        this.fromSendVehicleBizId = fromSendVehicleBizId;
    }

    public String getFromSendVehicleDetailBizId() {
        return fromSendVehicleDetailBizId;
    }

    public void setFromSendVehicleDetailBizId(String fromSendVehicleDetailBizId) {
        this.fromSendVehicleDetailBizId = fromSendVehicleDetailBizId;
    }

    public String getToSendVehicleBizId() {
        return toSendVehicleBizId;
    }

    public void setToSendVehicleBizId(String toSendVehicleBizId) {
        this.toSendVehicleBizId = toSendVehicleBizId;
    }

    public String getToSendVehicleDetailBizId() {
        return toSendVehicleDetailBizId;
    }

    public void setToSendVehicleDetailBizId(String toSendVehicleDetailBizId) {
        this.toSendVehicleDetailBizId = toSendVehicleDetailBizId;
    }
}
