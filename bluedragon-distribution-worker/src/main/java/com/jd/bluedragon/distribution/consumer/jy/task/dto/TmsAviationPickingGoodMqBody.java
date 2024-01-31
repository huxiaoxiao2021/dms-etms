package com.jd.bluedragon.distribution.consumer.jy.task.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2024/1/11 18:26
 * @Description
 */
public class TmsAviationPickingGoodMqBody implements Serializable {

    //主运单号
    private String tplBillCode;
    //航班号
    private String flightNumber;
    //始发机场
    private String beginNodeCode;
    //始发机场名称
    private String beginNodeName;
    //目的机场
    private String endNodeCode;
    //目的机场名称
    private String endNodeName;
    //计划起飞日期时间
    private Date planTakeOffTime;
    //计划降落日期时间
    private Date planTouchDownTime;
    //实际起飞日期时间
    private Date realTakeOffTime;
    //实际降落日期时间
    private Date realTouchDownTime;
    //操作类型   10 发货  20 部分改配 30 全部改配 40 起飞 50 降落 60提货
    private Integer operateType;
    //发货登记件数
    private Integer departCargoAmount;
    //发货实际重量
    private Double departCargoRealWeight;
    //批次信息
    private List<AirTransportBillDto> transbillList;
    //
    private Date operateTime;


    public TmsAviationPickingGoodMqBody() {
    }

    public String getTplBillCode() {
        return tplBillCode;
    }

    public void setTplBillCode(String tplBillCode) {
        this.tplBillCode = tplBillCode;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
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

    public Date getPlanTakeOffTime() {
        return planTakeOffTime;
    }

    public void setPlanTakeOffTime(Date planTakeOffTime) {
        this.planTakeOffTime = planTakeOffTime;
    }

    public Date getPlanTouchDownTime() {
        return planTouchDownTime;
    }

    public void setPlanTouchDownTime(Date planTouchDownTime) {
        this.planTouchDownTime = planTouchDownTime;
    }

    public Date getRealTakeOffTime() {
        return realTakeOffTime;
    }

    public void setRealTakeOffTime(Date realTakeOffTime) {
        this.realTakeOffTime = realTakeOffTime;
    }

    public Date getRealTouchDownTime() {
        return realTouchDownTime;
    }

    public void setRealTouchDownTime(Date realTouchDownTime) {
        this.realTouchDownTime = realTouchDownTime;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Integer getDepartCargoAmount() {
        return departCargoAmount;
    }

    public void setDepartCargoAmount(Integer departCargoAmount) {
        this.departCargoAmount = departCargoAmount;
    }

    public Double getDepartCargoRealWeight() {
        return departCargoRealWeight;
    }

    public void setDepartCargoRealWeight(Double departCargoRealWeight) {
        this.departCargoRealWeight = departCargoRealWeight;
    }

    public List<AirTransportBillDto> getTransbillList() {
        return transbillList;
    }

    public void setTransbillList(List<AirTransportBillDto> transbillList) {
        this.transbillList = transbillList;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
