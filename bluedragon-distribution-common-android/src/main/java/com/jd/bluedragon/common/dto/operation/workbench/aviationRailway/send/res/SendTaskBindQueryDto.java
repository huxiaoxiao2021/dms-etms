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

    private Double weight;

    private Double volume;
    /**
     * 件数
     */
    private Integer itemNum;
    /**
     * 运力编码
     */
    private String transportCode;
    /**
     * 标准发车时间
     */
    private String departureTimeStr;
    /**
     * 目的分拣中心
     */
    private Integer nextSiteId;
    private String nextSiteName;
    /**
     * 货物类型
     * CargoTypeEnum
     */
    private Integer cargoType;
    /**
     * 航空类型
     * AirTypeEnum
     */
    private Integer airType;


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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Integer getItemNum() {
        return itemNum;
    }

    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getDepartureTimeStr() {
        return departureTimeStr;
    }

    public void setDepartureTimeStr(String departureTimeStr) {
        this.departureTimeStr = departureTimeStr;
    }

    public Integer getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Integer nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public String getNextSiteName() {
        return nextSiteName;
    }

    public void setNextSiteName(String nextSiteName) {
        this.nextSiteName = nextSiteName;
    }

    public Integer getCargoType() {
        return cargoType;
    }

    public void setCargoType(Integer cargoType) {
        this.cargoType = cargoType;
    }

    public Integer getAirType() {
        return airType;
    }

    public void setAirType(Integer airType) {
        this.airType = airType;
    }
}
