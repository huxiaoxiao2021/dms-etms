package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/14 20:54
 * @Description
 */
public class AviationSealListDto implements Serializable {

    private static final long serialVersionUID = -5005890642092421853L;

    private String bizId;
    private String detailBizId;
    /**
     * 订舱号
     */
    private String bookingCode;
    /**
     * 航班号
     */
    private String flightNumber;
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
    /**
     * 目的分拣中心
     */
    private Integer nextSiteId;
    private String nextSiteName;

    private Double weight;
    private Double volume;
    /**
     * 件数
     */
    private Integer itemNum;
    /**
     * 标准发车时间
     */
    private String departureTimeStr;
    /**
     * 运力编码
     */
    private String transportCode;
    /**
     * 绑定标识
     */
    private Boolean bindFlag;
    /**
     * 是否无任务；0-否 1-是
     */
    private Integer manualCreatedFlag;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
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

    public String getDepartureTimeStr() {
        return departureTimeStr;
    }

    public void setDepartureTimeStr(String departureTimeStr) {
        this.departureTimeStr = departureTimeStr;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public Boolean getBindFlag() {
        return bindFlag;
    }

    public void setBindFlag(Boolean bindFlag) {
        this.bindFlag = bindFlag;
    }

    public String getDetailBizId() {
        return detailBizId;
    }

    public void setDetailBizId(String detailBizId) {
        this.detailBizId = detailBizId;
    }

    public Integer getManualCreatedFlag() {
        return manualCreatedFlag;
    }

    public void setManualCreatedFlag(Integer manualCreatedFlag) {
        this.manualCreatedFlag = manualCreatedFlag;
    }
}
