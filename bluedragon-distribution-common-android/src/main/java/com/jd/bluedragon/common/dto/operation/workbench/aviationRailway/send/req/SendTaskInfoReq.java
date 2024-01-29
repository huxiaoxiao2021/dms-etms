package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/12/4 18:27
 * @Description
 */
public class SendTaskInfoReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 4784612639942744950L;


    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }
}
