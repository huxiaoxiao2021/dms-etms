package com.jd.bluedragon.distribution.transport.domain;

/**
 * <p>
 * Created by lixin39 on 2017/12/29.
 */
public class ArTransportInfo {

    /**
     * 航空公司编号/铁路担当局编号
     */
    private String transCompanyCode;

    /**
     * 航空公司/铁路担当局名称
     */
    private String transCompany;

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
    private String startStationId;

    /**
     * 落地机场
     */
    private String endStationName;

    /**
     * 落地机场编号
     */
    private String endStationId;

    /**
     * 预计起飞时间
     */
    private String planStartTime;

    /**
     * 预计落地时间
     */
    private String planEndTime;

    /**
     * 时效（跨越天数）
     */
    private Integer aging;

    public String getTransCompanyCode() {
        return transCompanyCode;
    }

    public void setTransCompanyCode(String transCompanyCode) {
        this.transCompanyCode = transCompanyCode;
    }

    public String getTransCompany() {
        return transCompany;
    }

    public void setTransCompany(String transCompany) {
        this.transCompany = transCompany;
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

    public String getStartStationId() {
        return startStationId;
    }

    public void setStartStationId(String startStationId) {
        this.startStationId = startStationId;
    }

    public String getEndStationName() {
        return endStationName;
    }

    public void setEndStationName(String endStationName) {
        this.endStationName = endStationName;
    }

    public String getEndStationId() {
        return endStationId;
    }

    public void setEndStationId(String endStationId) {
        this.endStationId = endStationId;
    }

    public String getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(String planStartTime) {
        this.planStartTime = planStartTime;
    }

    public String getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(String planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Integer getAging() {
        return aging;
    }

    public void setAging(Integer aging) {
        this.aging = aging;
    }

}
