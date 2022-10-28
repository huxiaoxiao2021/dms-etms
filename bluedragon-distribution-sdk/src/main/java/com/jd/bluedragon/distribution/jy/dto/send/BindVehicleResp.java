package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;

public class BindVehicleResp implements Serializable {

    private static final long serialVersionUID = -3631878177602459614L;

    /**
     * 迁移前的子任务编号
     */
    private String fromSendVehicleDetailBizId;

    /**
     * 迁移后的子任务编号
     */
    private String toSendVehicleDetailBizId;

    public String getFromSendVehicleDetailBizId() {
        return fromSendVehicleDetailBizId;
    }

    public void setFromSendVehicleDetailBizId(String fromSendVehicleDetailBizId) {
        this.fromSendVehicleDetailBizId = fromSendVehicleDetailBizId;
    }
}
