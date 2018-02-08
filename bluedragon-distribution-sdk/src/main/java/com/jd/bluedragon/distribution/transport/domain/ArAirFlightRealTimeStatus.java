package com.jd.bluedragon.distribution.transport.domain;

import java.util.Date;

/**
 * 空铁航空飞机实际起飞/降落时间
 * <p>
 * <p>
 * Created by lixin39 on 2018/2/5.
 */
public class ArAirFlightRealTimeStatus {

    /**
     * 航班号
     */
    private String flightNumber;

    /**
     * 飞行日期
     */
    private Date filghtDate;

    /**
     * 起飞20 ,降落30
     */
    private Integer status;

    /**
     * 实际时间（实际起飞或降落时间）
     */
    private Date realTime;

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
     * 预计起飞时间 格式：HH:mm
     */
    private String takeOffTime;

    /**
     * 预计到达时间 格式：HH:mm
     */
    private String touchDownTime;

    /**
     * 是否延误 0-正常，1-延误
     */
    private Integer delayFlag;

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Date getFilghtDate() {
        return filghtDate;
    }

    public void setFilghtDate(Date filghtDate) {
        this.filghtDate = filghtDate;
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
}
