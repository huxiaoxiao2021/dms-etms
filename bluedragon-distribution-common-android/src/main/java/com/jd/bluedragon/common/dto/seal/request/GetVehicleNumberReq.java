package com.jd.bluedragon.common.dto.seal.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class GetVehicleNumberReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 4321113548553619966L;
    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 任务编码
     */
    private String transWorkItemCode;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }
}
