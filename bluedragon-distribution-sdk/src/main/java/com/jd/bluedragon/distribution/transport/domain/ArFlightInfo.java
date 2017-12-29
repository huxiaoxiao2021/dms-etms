package com.jd.bluedragon.distribution.transport.domain;

import java.util.Date;

/**
 * <p>
 * Created by lixin39 on 2017/12/29.
 */
public class ArFlightInfo {

    /**
     * 航空公司
     */
    private String airlineCompany;

    /**
     * 起飞城市
     */
    private String startCityName;

    /**
     * 起飞城市编号
     */
    private Integer startCityId;

    /**
     * 落地城市
     */
    private String endCityName;

    /**
     * 落地城市编号
     */
    private Integer endCityId;

    /**
     * 起飞机场
     */
    private String startStationName;

    /**
     * 起飞机场编号
     */
    private Integer startStationId;

    /**
     * 落地机场
     */
    private String endStationName;

    /**
     * 落地机场编号
     */
    private Integer endStationId;

    /**
     * 预计起飞时间
     */
    private Date planStartTime;

    /**
     * 预计落地时间
     */
    private Date planEndTime;

    public String getAirlineCompany() {
        return airlineCompany;
    }

    public void setAirlineCompany(String airlineCompany) {
        this.airlineCompany = airlineCompany;
    }

    public String getStartCityName() {
        return startCityName;
    }

    public void setStartCityName(String startCityName) {
        this.startCityName = startCityName;
    }

    public Integer getStartCityId() {
        return startCityId;
    }

    public void setStartCityId(Integer startCityId) {
        this.startCityId = startCityId;
    }

    public String getEndCityName() {
        return endCityName;
    }

    public void setEndCityName(String endCityName) {
        this.endCityName = endCityName;
    }

    public Integer getEndCityId() {
        return endCityId;
    }

    public void setEndCityId(Integer endCityId) {
        this.endCityId = endCityId;
    }

    public String getStartStationName() {
        return startStationName;
    }

    public void setStartStationName(String startStationName) {
        this.startStationName = startStationName;
    }

    public Integer getStartStationId() {
        return startStationId;
    }

    public void setStartStationId(Integer startStationId) {
        this.startStationId = startStationId;
    }

    public String getEndStationName() {
        return endStationName;
    }

    public void setEndStationName(String endStationName) {
        this.endStationName = endStationName;
    }

    public Integer getEndStationId() {
        return endStationId;
    }

    public void setEndStationId(Integer endStationId) {
        this.endStationId = endStationId;
    }

    public Date getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }
}
