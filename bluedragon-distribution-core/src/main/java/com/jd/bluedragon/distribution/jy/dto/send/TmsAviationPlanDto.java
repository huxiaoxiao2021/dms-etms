package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zhengchengfa
 * @Date 2023/8/7 10:55
 * @Description 运输航空计划
 *
 *
 * booking_code 订舱号  varchar(32)
 * start_node_code 始发分拣编码 varchar(32)
 * start_node_name 始发分拣名称 varchar(32)
 * flight_number 航班号 varchar(32)
 * take_off_time 起飞时间 datetime
 * touch_down_time 降落时间 datetime
 * air_company_code 航空公司编码 varchar(16)
 * air_company_name 航空公司名称 varchar(32)
 * begin_node_code 始发机场编码 varchar(16)
 * begin_node_name 始发机场名称 varchar(32)
 * end_node_code 目的机场编码 varchar(16)
 * end_node_name 目的机场名称 varchar(32)
 * carrier_code 承运商编码 varchar(32)
 * carrier_name 承运商名称 varchar(64)
 * booking_weight 订舱货量 double
 * cargo_type 货物类型 int(11)
 * air_type 航空类型 int(11)
 * booked_status 订舱状态 int(11)
 */
public class TmsAviationPlanDto implements Serializable {

    private static final long serialVersionUID = -8024388953489536391L;

    private String businessId;

    private String bookingCode;//订舱号
    private String startNodeCode;//始发分拣编码
    private String startNodeName;
    private String flightNumber;//航班号
    private String takeOffTime;//起飞时间
    private String touchDownTime;
    private String airCompanyCode;//航空公司编码
    private String airCompanyName;
    private String beginNodeCode;//始发机场编码
    private String beginNodeName;
    private String endNodeCode;//目的机场编码
    private String endNodeName;
    private String carrierCode;//承运商编码
    private String carrierName;
    private Double bookingWeight;//订舱货量
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
    //    订舱状态
    private Integer bookedStatus;
    //操作时间
    private Date operateTime;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getStartNodeCode() {
        return startNodeCode;
    }

    public void setStartNodeCode(String startNodeCode) {
        this.startNodeCode = startNodeCode;
    }

    public String getStartNodeName() {
        return startNodeName;
    }

    public void setStartNodeName(String startNodeName) {
        this.startNodeName = startNodeName;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
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

    public Integer getBookedStatus() {
        return bookedStatus;
    }

    public void setBookedStatus(Integer bookedStatus) {
        this.bookedStatus = bookedStatus;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
