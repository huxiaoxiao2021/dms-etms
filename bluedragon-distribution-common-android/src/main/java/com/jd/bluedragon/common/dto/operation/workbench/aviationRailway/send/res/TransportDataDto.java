package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 16:26
 * @Description
 */
public class TransportDataDto implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private String transportCode;
    /**
     * 出发时间
     */
    private Long departureTime;
    private String departureTimeStr;
    /**
     * 是否关注打标（最近的运力重点关注）
     */
    private Boolean focusFlag;

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public Long getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Long departureTime) {
        this.departureTime = departureTime;
    }

    public Boolean getFocusFlag() {
        return focusFlag;
    }

    public void setFocusFlag(Boolean focusFlag) {
        this.focusFlag = focusFlag;
    }

    public String getDepartureTimeStr() {
        return departureTimeStr;
    }

    public void setDepartureTimeStr(String departureTimeStr) {
        this.departureTimeStr = departureTimeStr;
    }
}
