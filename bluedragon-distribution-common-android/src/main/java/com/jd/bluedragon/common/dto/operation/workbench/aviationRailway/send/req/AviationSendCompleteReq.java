package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @author liwenji
 * @description 
 * @date 2023-08-24 18:26
 */
public class AviationSendCompleteReq extends BaseReq  implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 发车任务bizId
     */
    private String sendVehicleBizId;

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }
}
