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
    private String departureTimeStr;
    /**
     * 是否关注打标（最近的运力重点关注）
     */
    private Boolean focusFlag;

    /**
     * 线路类型
     *
     */
    private Integer transType;
    private String transTypeName;
    /**
     * 运输方式
     */
    private Integer transWay;
    private String transWayName;

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
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

    public Integer getTransType() {
        return transType;
    }
    public void setTransType(Integer transType) {
        this.transType = transType;
    }

    public String getTransTypeName() {
        return transTypeName;
    }

    public void setTransTypeName(String transTypeName) {
        this.transTypeName = transTypeName;
    }

    public Integer getTransWay() {
        return transWay;
    }

    public void setTransWay(Integer transWay) {
        this.transWay = transWay;
    }

    public String getTransWayName() {
        return transWayName;
    }

    public void setTransWayName(String transWayName) {
        this.transWayName = transWayName;
    }
}
