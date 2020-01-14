package com.jd.bluedragon.common.dto.air.response;

import java.io.Serializable;
import java.util.Date;

/**
 * 主运单详情
 * @author : xumigen
 * @date : 2019/11/4
 */
public class AirTplBillRespDto implements Serializable {
    private static final long serialVersionUID = 1L;


    private String tplBillCode;

    private String flightNumber;

    private String beginNodeName;

    private String endNodeName;

    private Date planTakeOffTime;

    private String departCargoTypeName;

    private int departCargoAmount;

    private Double departCargoRealWeight;

    private Double departCargoChargedWeight;

    private String departRemark;

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

    public String getBeginNodeName() {
        return beginNodeName;
    }

    public void setBeginNodeName(String beginNodeName) {
        this.beginNodeName = beginNodeName;
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

    public String getDepartCargoTypeName() {
        return departCargoTypeName;
    }

    public void setDepartCargoTypeName(String departCargoTypeName) {
        this.departCargoTypeName = departCargoTypeName;
    }

    public int getDepartCargoAmount() {
        return departCargoAmount;
    }

    public void setDepartCargoAmount(int departCargoAmount) {
        this.departCargoAmount = departCargoAmount;
    }

    public Double getDepartCargoRealWeight() {
        return departCargoRealWeight;
    }

    public void setDepartCargoRealWeight(Double departCargoRealWeight) {
        this.departCargoRealWeight = departCargoRealWeight;
    }

    public Double getDepartCargoChargedWeight() {
        return departCargoChargedWeight;
    }

    public void setDepartCargoChargedWeight(Double departCargoChargedWeight) {
        this.departCargoChargedWeight = departCargoChargedWeight;
    }

    public String getDepartRemark() {
        return departRemark;
    }

    public void setDepartRemark(String departRemark) {
        this.departRemark = departRemark;
    }
}
