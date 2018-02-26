package com.jd.bluedragon.distribution.transport.domain;

import java.util.Date;

/**
 * 运单维度航空起飞/降落信息
 * <p>
 * Created by lixin39 on 2018/2/5.
 */
public class ArAirWaybillStatus {

    /**
     * 航班号
     */
    private String flightNumber;

    /**
     * 起飞20 ,降落30
     */
    private Integer status;

    /**
     * 实际时间
     */
    private Date realTime;

    /**
     * 发车条码
     */
    private String sendCarCode;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 运单号
     */
    private String wayBillCode;

    /**
     * 运输方式
     */
    private String transWay;

    /**
     * 始发机场编码
     */
    private String beginNodeCode;

    /**
     * 始发机场名称
     */
    private String beginNodeName;

    /**
     * 目的机场编码
     */
    private String endNodeCode;

    /**
     * 目的机场名称
     */
    private String endNodeName;

    /**
     * 预计起飞时间
     */
    private String takeOffTime;

    /**
     * 预计到达时间
     */
    private String touchDownTime;

    /**
     * 是否延误
     */
    private Integer delayFlag;

    /**
     * 运力编码
     */
    private String transportCode;

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getRealTime() {
        return realTime;
    }

    public void setRealTime(Date realTime) {
        this.realTime = realTime;
    }

    public String getSendCarCode() {
        return sendCarCode;
    }

    public void setSendCarCode(String sendCarCode) {
        this.sendCarCode = sendCarCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getWayBillCode() {
        return wayBillCode;
    }

    public void setWayBillCode(String wayBillCode) {
        this.wayBillCode = wayBillCode;
    }

    public String getTransWay() {
        return transWay;
    }

    public void setTransWay(String transWay) {
        this.transWay = transWay;
    }

    public String getBeginNodeCode() {
        return beginNodeCode;
    }

    public void setBeginNodeCode(String beginNodeCode) {
        this.beginNodeCode = beginNodeCode;
    }

    public String getBeginNodeName() {
        return beginNodeName;
    }

    public void setBeginNodeName(String beginNodeName) {
        this.beginNodeName = beginNodeName;
    }

    public String getEndNodeCode() {
        return endNodeCode;
    }

    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public String getEndNodeName() {
        return endNodeName;
    }

    public void setEndNodeName(String endNodeName) {
        this.endNodeName = endNodeName;
    }

    public String getTakeOffTime() {
        return takeOffTime;
    }

    public void setTakeOffTime(String takeOffTime) {
        this.takeOffTime = takeOffTime;
    }

    public String getTouchDownTime() {
        return touchDownTime;
    }

    public void setTouchDownTime(String touchDownTime) {
        this.touchDownTime = touchDownTime;
    }

    public Integer getDelayFlag() {
        return delayFlag;
    }

    public void setDelayFlag(Integer delayFlag) {
        this.delayFlag = delayFlag;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

}
