package com.jd.bluedragon.core.jmq.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 为FXM提供 铁路计费要素信息的对象
 * @author : xumigen
 * @date : 2019/9/13
 */
public class RailwaySendRegistCostFxmDto {

    /**
     * 发货登记日期
     */
    private Date sendDate;

    /**
     * 主运单号
     */
    private String orderCode;

    /**
     * 车次
     */
    private String trainNumber;

    /**
     * 货物类型
     */
    private Integer goodsType;

    /**
     * 货物类型
     */
    private String goodsTypeName;

    /**
     * 始发车站
     */
    private String startStationCode;

    private String startStationCodeName;

    /**
     * 目的车站
     */
    private String endStationCode;

    private String endStationCodeName;

    /**
     * 重量（单位：KG）
     */
    private BigDecimal weight;

    /**
     * 发货登记件数
     */
    private Integer sendNum;

    /**
     * 承运商
     */
    private String carrierCode;

    private String carrierName;

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getStartStationCode() {
        return startStationCode;
    }

    public void setStartStationCode(String startStationCode) {
        this.startStationCode = startStationCode;
    }

    public String getStartStationCodeName() {
        return startStationCodeName;
    }

    public void setStartStationCodeName(String startStationCodeName) {
        this.startStationCodeName = startStationCodeName;
    }

    public String getEndStationCode() {
        return endStationCode;
    }

    public void setEndStationCode(String endStationCode) {
        this.endStationCode = endStationCode;
    }

    public String getEndStationCodeName() {
        return endStationCodeName;
    }

    public void setEndStationCodeName(String endStationCodeName) {
        this.endStationCodeName = endStationCodeName;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
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

    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsTypeName() {
        return goodsTypeName;
    }

    public void setGoodsTypeName(String goodsTypeName) {
        this.goodsTypeName = goodsTypeName;
    }
}
