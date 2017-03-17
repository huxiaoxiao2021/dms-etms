package com.jd.bluedragon.distribution.carSchedule.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * TMS的发车报文 TO与PO之间的映射关系
 * Created by wuzuxiang on 2017/3/4.
 */
public class CarScheduleTo {

    /**
     * 发车条码
     */
    @Expose
    @SerializedName("sendCarCode")
    private String carSendCode;

    /**
     * 车牌号
     */
    @Expose
    @SerializedName("vehicleNumber")
    private String vehicleNumber;

    /**
     * 发车始发地(七位站点编码)
     */
    @Expose
    @SerializedName("startSiteCode")
    private String createDmsCode;

    /**
     * 始发站点ID
     */
    @Expose
    private Integer createSiteCode;

    /**
     * 发车始发地名称
     */
    @Expose
    private String createSiteName;

    /**
     * 发车目的地(七位站点编码)
     */
    @Expose
    @SerializedName("endSiteCode")
    private String receiveDmsCode;

    @Expose
    private Integer receiveSiteCode;

    /**
     * 发车目的地名称
     */
    @Expose
    private String receieSiteName;

    /**
     * 操作时间
     */
    @Expose
    @SerializedName("operateTime")
    private Date operateTime;

    /**
     * 标准发车时间
     */
    @Expose
    @SerializedName("standardSendTime")
    private Date standardSendTime;

    /**
     * 标准到达时间
     */
    @Expose
    @SerializedName("standardEndTime")
    private Date standardArriveTime;

    /**
     * 实际发车时间
     */
    @Expose
    @SerializedName("sendCarTime")
    private Date carSendTime;

    /**
     * 实际到达时间
     */
    @Expose
    @SerializedName("arriveCarTime")
    private Date carArriveTime;

    /**
     * 线路类型
     */
    @Expose
    @SerializedName("routeTypeCode")
    private Integer routeType;

    /**
     * 线路类型解释汉字
     */
    @Expose
    private String routeTypeMark;

    /**
     * 运输方式
     */
    @Expose
    @SerializedName("transportWay")
    private Integer transportWay;

    /**
     * 运输方式解释汉字
     */
    @Expose
    private String transportWayMark;

    /**
     * 类型：1自营  2三方
     */
    @Expose
    @SerializedName("carrierType")
    private Integer carrierType;

    /**
     * 车内批次号集合g
     */
    @Expose
    @SerializedName("batchCodeList")
    private String sendCodeList;

    /**
     * 车内包裹数量
     */
    @Expose
    @SerializedName("cargoVolumeSize")
    private Integer packageNum;

    /**
     * 车内的运单数量
     */
    @Expose
    @SerializedName("waybillList")
    private Integer waybillNum;

    public String getCarSendCode() {
        return carSendCode;
    }

    public void setCarSendCode(String carSendCode) {
        this.carSendCode = carSendCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getCreateDmsCode() {
        return createDmsCode;
    }

    public void setCreateDmsCode(String createDmsCode) {
        this.createDmsCode = createDmsCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getReceiveDmsCode() {
        return receiveDmsCode;
    }

    public void setReceiveDmsCode(String receiveDmsCode) {
        this.receiveDmsCode = receiveDmsCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceieSiteName() {
        return receieSiteName;
    }

    public void setReceieSiteName(String receieSiteName) {
        this.receieSiteName = receieSiteName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Date getStandardSendTime() {
        return standardSendTime;
    }

    public void setStandardSendTime(Date standardSendTime) {
        this.standardSendTime = standardSendTime;
    }

    public Date getStandardArriveTime() {
        return standardArriveTime;
    }

    public void setStandardArriveTime(Date standardArriveTime) {
        this.standardArriveTime = standardArriveTime;
    }

    public Date getCarSendTime() {
        return carSendTime;
    }

    public void setCarSendTime(Date carSendTime) {
        this.carSendTime = carSendTime;
    }

    public Date getCarArriveTime() {
        return carArriveTime;
    }

    public void setCarArriveTime(Date carArriveTime) {
        this.carArriveTime = carArriveTime;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    public String getRouteTypeMark() {
        return routeTypeMark;
    }

    public void setRouteTypeMark(String routeTypeMark) {
        this.routeTypeMark = routeTypeMark;
    }

    public Integer getTransportWay() {
        return transportWay;
    }

    public void setTransportWay(Integer transportWay) {
        this.transportWay = transportWay;
    }

    public String getTransportWayMark() {
        return transportWayMark;
    }

    public void setTransportWayMark(String transportWayMark) {
        this.transportWayMark = transportWayMark;
    }

    public Integer getCarrierType() {
        return carrierType;
    }

    public void setCarrierType(Integer carrierType) {
        this.carrierType = carrierType;
    }

    public String getSendCodeList() {
        return sendCodeList;
    }

    public void setSendCodeList(String sendCodeList) {
        this.sendCodeList = sendCodeList;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public Integer getWaybillNum() {
        return waybillNum;
    }

    public void setWaybillNum(Integer waybillNum) {
        this.waybillNum = waybillNum;
    }
}
