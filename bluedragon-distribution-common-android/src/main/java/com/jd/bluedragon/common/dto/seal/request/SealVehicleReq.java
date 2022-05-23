package com.jd.bluedragon.common.dto.seal.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

public class SealVehicleReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 6675404873010444219L;

    private Integer sealCarType;
    private String itemSimpleCode;
    private String transportCode;
    private List<String> batchCodes;
    private String vehicleNumber;
    private List<String> sealCodes;
    private Double weight;
    private Double volume;
    private String routeLineCode;
    private String sealCarTime;
    private Integer sealSiteId;
    private String sealSiteName;
    private String sealUserCode;
    private String sealUserName;
    private String palletCount;
    private Integer transWay;
    private String transWayName;
    private List<String> selectedSendCodes;
    private String billCode;

    public Integer getSealCarType() {
        return sealCarType;
    }

    public void setSealCarType(Integer sealCarType) {
        this.sealCarType = sealCarType;
    }

    public String getItemSimpleCode() {
        return itemSimpleCode;
    }

    public void setItemSimpleCode(String itemSimpleCode) {
        this.itemSimpleCode = itemSimpleCode;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public List<String> getBatchCodes() {
        return batchCodes;
    }

    public void setBatchCodes(List<String> batchCodes) {
        this.batchCodes = batchCodes;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public List<String> getSealCodes() {
        return sealCodes;
    }

    public void setSealCodes(List<String> sealCodes) {
        this.sealCodes = sealCodes;
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

    public String getRouteLineCode() {
        return routeLineCode;
    }

    public void setRouteLineCode(String routeLineCode) {
        this.routeLineCode = routeLineCode;
    }

    public String getSealCarTime() {
        return sealCarTime;
    }

    public void setSealCarTime(String sealCarTime) {
        this.sealCarTime = sealCarTime;
    }

    public Integer getSealSiteId() {
        return sealSiteId;
    }

    public void setSealSiteId(Integer sealSiteId) {
        this.sealSiteId = sealSiteId;
    }

    public String getSealSiteName() {
        return sealSiteName;
    }

    public void setSealSiteName(String sealSiteName) {
        this.sealSiteName = sealSiteName;
    }

    public String getSealUserCode() {
        return sealUserCode;
    }

    public void setSealUserCode(String sealUserCode) {
        this.sealUserCode = sealUserCode;
    }

    public String getSealUserName() {
        return sealUserName;
    }

    public void setSealUserName(String sealUserName) {
        this.sealUserName = sealUserName;
    }

    public String getPalletCount() {
        return palletCount;
    }

    public void setPalletCount(String palletCount) {
        this.palletCount = palletCount;
    }

    public Integer getTransWay() {
        return transWay;
    }

    public void setTransWay(Integer transWay) {
        this.transWay = transWay;
    }

    public String getTransWayName() {
        return transWayName;
    }

    public void setTransWayName(String transWayName) {
        this.transWayName = transWayName;
    }

    public List<String> getSelectedSendCodes() {
        return selectedSendCodes;
    }

    public void setSelectedSendCodes(List<String> selectedSendCodes) {
        this.selectedSendCodes = selectedSendCodes;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }
}
