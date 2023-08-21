package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/3 10:53
 * @Description
 */
public class SendTaskBindDto implements Serializable {
    private static final long serialVersionUID = 4784612639942744950L;

    private String bizId;
    private String detailBizId;
    /**
     * 航班号
     */
    private String flightNumber;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getDetailBizId() {
        return detailBizId;
    }

    public void setDetailBizId(String detailBizId) {
        this.detailBizId = detailBizId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
}
