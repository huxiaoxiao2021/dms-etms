package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 16:43
 * @Description  航空发货任务基础数据
 */
public class AviationSendTaskDto implements Serializable {

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
     * 起飞时间
     */
    private Long takeOffTime;
    /**
     * 航空公司编码
     */
    private String airCompanyCode;
    /**
     * 航空公司名称
     */
    private String airCompanyName;
    /**
     * 始发机场编码
     */
    private String beginNodeCode;
    /**
     * 始发机场名称
     */
    private String beginNodeName;
    /**
     * 承运商编码（货代）
     */
    private String carrierCode;
    /**
     * 承运商名称
     */
    private String carrierName;
    /**
     * 订舱货量（单位：kg）
     */
    private Double bookingWeight;
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

    /**
     * 任务下扫描重量
     */
    private Double scanWeight = 0d;


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

    public Long getTakeOffTime() {
        return takeOffTime;
    }

    public void setTakeOffTime(Long takeOffTime) {
        this.takeOffTime = takeOffTime;
    }

    public String getAirCompanyCode() {
        return airCompanyCode;
    }

    public void setAirCompanyCode(String airCompanyCode) {
        this.airCompanyCode = airCompanyCode;
    }

    public String getAirCompanyName() {
        return airCompanyName;
    }

    public void setAirCompanyName(String airCompanyName) {
        this.airCompanyName = airCompanyName;
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

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public Double getBookingWeight() {
        return bookingWeight;
    }

    public void setBookingWeight(Double bookingWeight) {
        this.bookingWeight = bookingWeight;
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

    public Double getScanWeight() {
        return scanWeight;
    }

    public void setScanWeight(Double scanWeight) {
        this.scanWeight = scanWeight;
    }

    public String getDetailBizId() {
        return detailBizId;
    }

    public void setDetailBizId(String detailBizId) {
        this.detailBizId = detailBizId;
    }
}
