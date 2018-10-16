package com.jd.bluedragon.distribution.transport.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2018年09月27日 18时:17分
 */
public class TransBookBillDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String transBookCode;
    private Integer status;
    private String statusName;
    private String beginNodeCode;
    private String beginNodeName;
    private Integer beginCityId;
    private String beginCityName;
    private String endNodeCode;
    private String endNodeName;
    private Integer endCityId;
    private String endCityName;
    private Date requirePickupTime;
    private String carrierCode;
    private String carrierName;
    private String vehicleNumber;
    private Integer transType;
    private Integer transWay;
    private Integer businessType;
    private String transTypeName;
    private String transWayName;
    private String businessTypeName;
    private String printUrl;

    public String getTransBookCode() {
        return this.transBookCode;
    }

    public void setTransBookCode(String transBookCode) {
        this.transBookCode = transBookCode;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return this.statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getBeginNodeCode() {
        return this.beginNodeCode;
    }

    public void setBeginNodeCode(String beginNodeCode) {
        this.beginNodeCode = beginNodeCode;
    }

    public String getBeginNodeName() {
        return this.beginNodeName;
    }

    public void setBeginNodeName(String beginNodeName) {
        this.beginNodeName = beginNodeName;
    }

    public Integer getBeginCityId() {
        return this.beginCityId;
    }

    public void setBeginCityId(Integer beginCityId) {
        this.beginCityId = beginCityId;
    }

    public String getBeginCityName() {
        return this.beginCityName;
    }

    public void setBeginCityName(String beginCityName) {
        this.beginCityName = beginCityName;
    }

    public String getEndNodeCode() {
        return this.endNodeCode;
    }

    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public String getEndNodeName() {
        return this.endNodeName;
    }

    public void setEndNodeName(String endNodeName) {
        this.endNodeName = endNodeName;
    }

    public Integer getEndCityId() {
        return this.endCityId;
    }

    public void setEndCityId(Integer endCityId) {
        this.endCityId = endCityId;
    }

    public String getEndCityName() {
        return this.endCityName;
    }

    public void setEndCityName(String endCityName) {
        this.endCityName = endCityName;
    }

    public Date getRequirePickupTime() {
        return this.requirePickupTime;
    }

    public void setRequirePickupTime(Date requirePickupTime) {
        this.requirePickupTime = requirePickupTime;
    }

    public String getCarrierCode() {
        return this.carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getCarrierName() {
        return this.carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getVehicleNumber() {
        return this.vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getTransType() {
        return this.transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
    }

    public Integer getTransWay() {
        return this.transWay;
    }

    public void setTransWay(Integer transWay) {
        this.transWay = transWay;
    }

    public Integer getBusinessType() {
        return this.businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getTransTypeName() {
        return this.transTypeName;
    }

    public void setTransTypeName(String transTypeName) {
        this.transTypeName = transTypeName;
    }

    public String getTransWayName() {
        return this.transWayName;
    }

    public void setTransWayName(String transWayName) {
        this.transWayName = transWayName;
    }

    public String getBusinessTypeName() {
        return this.businessTypeName;
    }

    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
    }

    public String getPrintUrl() {
        return printUrl;
    }

    public void setPrintUrl(String printUrl) {
        this.printUrl = printUrl;
    }
}
