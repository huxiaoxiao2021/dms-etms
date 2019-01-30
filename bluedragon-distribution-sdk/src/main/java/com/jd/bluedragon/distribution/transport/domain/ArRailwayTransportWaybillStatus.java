package com.jd.bluedragon.distribution.transport.domain;

import java.util.Date;

/**
 * 包裹维度铁路运输运单维度信息
 * <p>
 * Created by lixin39 on 2018/2/5.
 */
public class ArRailwayTransportWaybillStatus {

    /**
     * 中铁运单号
     */
    private String creTransbillCode;

    /**
     * 车次号
     */
    private String trainNumber;

    /**
     * 铁路类型：1 - 高铁，2 - 行包
     */
    private Integer railwayType;

    /**
     * 节点名称：0 承运制票、20 装车确认、30 卸车确认、40 提货交付
     */
    private Integer status;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 出发城市编号
     */
    private Integer beginCityCode;

    /**
     * 出发城市
     */
    private String beginCityName;

    /**
     * 出发车站, 城市下的车站（装车确认、卸车确认 两节点 必填）
     */
    private String beginStationName;

    /**
     * 目的城市编码
     */
    private Integer endCityCode;

    /**
     * 目的城市
     */
    private String endCityName;

    /**
     * 目的车站, 城市下的车站（装车确认、卸车确认 两节点 必填）
     */
    private String endStationName;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 运单号
     */
    private String wayBillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 发车条码
     */
    private String sendCarCode;

    /**
     * 运力编码
     */
    private String transportCode;

    /**
     * 路由线路编码
     */
    private String routeLineCode;

    public String getCreTransbillCode() {
        return creTransbillCode;
    }

    public void setCreTransbillCode(String creTransbillCode) {
        this.creTransbillCode = creTransbillCode;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public Integer getRailwayType() {
        return railwayType;
    }

    public void setRailwayType(Integer railwayType) {
        this.railwayType = railwayType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getBeginCityCode() {
        return beginCityCode;
    }

    public void setBeginCityCode(Integer beginCityCode) {
        this.beginCityCode = beginCityCode;
    }

    public String getBeginCityName() {
        return beginCityName;
    }

    public void setBeginCityName(String beginCityName) {
        this.beginCityName = beginCityName;
    }

    public String getBeginStationName() {
        return beginStationName;
    }

    public void setBeginStationName(String beginStationName) {
        this.beginStationName = beginStationName;
    }

    public Integer getEndCityCode() {
        return endCityCode;
    }

    public void setEndCityCode(Integer endCityCode) {
        this.endCityCode = endCityCode;
    }

    public String getEndCityName() {
        return endCityName;
    }

    public void setEndCityName(String endCityName) {
        this.endCityName = endCityName;
    }

    public String getEndStationName() {
        return endStationName;
    }

    public void setEndStationName(String endStationName) {
        this.endStationName = endStationName;
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

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getSendCarCode() {
        return sendCarCode;
    }

    public void setSendCarCode(String sendCarCode) {
        this.sendCarCode = sendCarCode;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getRouteLineCode() {
        return routeLineCode;
    }

    public void setRouteLineCode(String routeLineCode) {
        this.routeLineCode = routeLineCode;
    }
}
