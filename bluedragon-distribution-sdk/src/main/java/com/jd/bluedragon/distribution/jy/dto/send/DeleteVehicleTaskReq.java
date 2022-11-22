package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

public class DeleteVehicleTaskReq extends JyReqBaseDto implements Serializable {
    private static final long serialVersionUID = 2151425704342449497L;
    /**
     * 业务主键（主任务号）
     */
    private String bizId;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
