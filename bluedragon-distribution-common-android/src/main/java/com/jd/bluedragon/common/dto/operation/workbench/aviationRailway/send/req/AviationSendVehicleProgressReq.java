package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

/**
 * @author liwenji
 * @description 发货任务详情请求
 * @date 2023-08-17 14:04
 */
public class AviationSendVehicleProgressReq {

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
