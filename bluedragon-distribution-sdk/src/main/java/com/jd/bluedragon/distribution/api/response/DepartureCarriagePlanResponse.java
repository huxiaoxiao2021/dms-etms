package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * Created by guoyongzhi on 2017/10/1.
 */
public class DepartureCarriagePlanResponse  extends JdResponse {

    /**
     * 运输计划号
     */
        private String carriagePlanCode;
    /**
     * 始发地
     */
        private  String beginSiteName;
    /**
     *承运商编码
     */
        private  String carrierCode;
    /**
     *承运商名称
     */
        private  String carrierName;
    /**
     *承运商车队编码
     */
    private  String carrierTeamCode;
    /**
     *承运商车队名称
     */
    private  String carrierTeamName;
    /**
     *订单数
     * 该运输计划下的订单数
     */
    private  Integer orderNum;
    /**
     *司机账号
     */
    private  String driverCode;
    /**
     *司机姓名
     */
    private  String driverName;
    /**
     *司机驾驶证号
     */
    private  String drivingLicense;
    /**
     *卡位号
     */
    private  String parkingSpaceNum;
    /**
     *城运类型
     * 1-分拣集货，2-仓库直发
     */
    private  Integer transMode;

    public String getCarriagePlanCode() {
        return carriagePlanCode;
    }

    public void setCarriagePlanCode(String carriagePlanCode) {
        this.carriagePlanCode = carriagePlanCode;
    }

    public String getBeginSiteName() {
        return beginSiteName;
    }

    public void setBeginSiteName(String beginSiteName) {
        this.beginSiteName = beginSiteName;
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

    public String getCarrierTeamCode() {
        return carrierTeamCode;
    }

    public void setCarrierTeamCode(String carrierTeamCode) {
        this.carrierTeamCode = carrierTeamCode;
    }

    public String getCarrierTeamName() {
        return carrierTeamName;
    }

    public void setCarrierTeamName(String carrierTeamName) {
        this.carrierTeamName = carrierTeamName;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(String driverCode) {
        this.driverCode = driverCode;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    public String getParkingSpaceNum() {
        return parkingSpaceNum;
    }

    public void setParkingSpaceNum(String parkingSpaceNum) {
        this.parkingSpaceNum = parkingSpaceNum;
    }

    public Integer getTransMode() {
        return transMode;
    }

    public void setTransMode(Integer transMode) {
        this.transMode = transMode;
    }
}
