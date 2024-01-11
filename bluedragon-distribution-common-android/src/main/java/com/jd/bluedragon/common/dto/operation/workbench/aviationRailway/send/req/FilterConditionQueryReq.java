package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 15:30
 * @Description
 */
public class FilterConditionQueryReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 状态
     * JyAviationRailwaySendVehicleStatusEnum
     */
    private Integer statusCode;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
