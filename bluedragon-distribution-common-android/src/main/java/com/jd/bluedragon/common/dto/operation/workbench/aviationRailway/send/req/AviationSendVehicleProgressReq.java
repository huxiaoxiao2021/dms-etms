package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import java.io.Serializable;

/**
 * @author liwenji
 * @description 发货任务详情请求
 * @date 2023-08-17 14:04
 */
public class AviationSendVehicleProgressReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 业务主键
     */
    private String bizId;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
