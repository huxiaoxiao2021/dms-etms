package com.jd.bluedragon.distribution.transport.domain;

import java.io.Serializable;

/**
 * @author lixin39
 * @Description 铁路运输订单信息
 * @ClassName ArCreTransportBillTrace
 * @date 2019/1/21
 */
public class ArCreTransportBillTrace implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 中铁运单号
     */
    private String creTransbillCode;

    /**
     * 铁路类型：1 - 高铁，2 - 行包
     */
    private Integer railwayType;

    /**
     * 节点名称：10 承运制票、20 装车确认、30 卸车确认、40 提货交付
     */
    private Integer status;

    /**
     * 车次号
     */
    private String trainNumber;

    /**
     * 出发城市
     */
    private String beginCityName;

    /**
     * 出发车站, 城市下的车站（装车确认、卸车确认 两节点 必填）
     */
    private String beginStationName;

    /**
     * 目的城市
     */
    private String endCityName;

    /**
     * 目的车站, 城市下的车站（装车确认、卸车确认 两节点 必填）
     */
    private String endStationName;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 操作时间
     */
    private Long operateTime;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreTransbillCode() {
        return creTransbillCode;
    }

    public void setCreTransbillCode(String creTransbillCode) {
        this.creTransbillCode = creTransbillCode;
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

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
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

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
