package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/16 15:38
 * @Description
 */
public class SendTaskBindQueryDto implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private String bindBizId;
    private String bindDetailBizId;
    /**
     * 航班号
     */
    private String flightNumber;

    public String getBindBizId() {
        return bindBizId;
    }

    public void setBindBizId(String bindBizId) {
        this.bindBizId = bindBizId;
    }

    public String getBindDetailBizId() {
        return bindDetailBizId;
    }

    public void setBindDetailBizId(String bindDetailBizId) {
        this.bindDetailBizId = bindDetailBizId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
}
