package com.jd.bluedragon.distribution.transport.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 待收货查询信息实体
 * Created by xumei3 on 2018/1/2.
 */
public class ArWaitReceive implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 运输类型，1-航空，2-铁路 */
    private Integer transportType;

    /** 航空单号/铁路单号 */
    private String orderCode;

    /** 运力名称 */
    private String transportName;

    /** 航空公司 */
    private String airlineCompany;

    /** 起飞城市 */
    private String startCityName;

    /** 起飞城市编号 */
    private Integer startCityId;

    /** 落地城市 */
    private String endCityName;

    /** 落地城市编号 */
    private Integer endCityId;

    /** 起飞机场 */
    private String startStationName;

    /** 起飞机场编号 */
    private Integer startStationId;

    /** 落地机场 */
    private String endStationName;

    /** 落地机场编号 */
    private Integer endStationId;

    /** 预计起飞时间 */
    private String planStartTime;

    /** 预计落地时间 */
    private String planEndTime;

    public ArWaitReceive(String orderCode,String transportName,String startStationName,
                         String endStationName,String planStartTime,String planEndTime){
        this.orderCode = orderCode;
        this.transportName = transportName;
        this.startStationName = startStationName;
        this.endStationName = endStationName;
        this.planStartTime = planStartTime;
        this.planEndTime = planEndTime;
    }

    public String getAirlineCompany() {
        return airlineCompany;
    }

    public void setAirlineCompany(String airlineCompany) {
        this.airlineCompany = airlineCompany;
    }

    public Integer getEndCityId() {
        return endCityId;
    }

    public void setEndCityId(Integer endCityId) {
        this.endCityId = endCityId;
    }

    public String getEndCityName() {
        return endCityName;
    }

    public void setEndCityName(String endCityName) {
        this.endCityName = endCityName;
    }

    public Integer getEndStationId() {
        return endStationId;
    }

    public void setEndStationId(Integer endStationId) {
        this.endStationId = endStationId;
    }

    public String getEndStationName() {
        return endStationName;
    }

    public void setEndStationName(String endStationName) {
        this.endStationName = endStationName;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(String planEndTime) {
        this.planEndTime = planEndTime;
    }

    public String getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(String planStartTime) {
        this.planStartTime = planStartTime;
    }

    public Integer getStartCityId() {
        return startCityId;
    }

    public void setStartCityId(Integer startCityId) {
        this.startCityId = startCityId;
    }

    public String getStartCityName() {
        return startCityName;
    }

    public void setStartCityName(String startCityName) {
        this.startCityName = startCityName;
    }

    public Integer getStartStationId() {
        return startStationId;
    }

    public void setStartStationId(Integer startStationId) {
        this.startStationId = startStationId;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public String getStartStationName() {
        return startStationName;
    }

    public void setStartStationName(String startStationName) {
        this.startStationName = startStationName;
    }
}
