package com.jd.bluedragon.common.dto.air.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * 分拣发货登记提交 对象
 * @author : xumigen
 * @date : 2019/11/4
 */
public class AirDepartRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private CurrentOperate currentOperate;
    private User user;

    private String tplBillCode;

    private String flightNumber;

    private long flightDate;

    private String beginNodeCode;

    private String endNodeCode;

    private int departCargoType;

    private int departCargoAmount;

    private double departCargoRealWeight;

    private double departCargoChargedWeight;

    private String departRemark;

    private List<String> batchCodes;

    private String operateNodeCode;

    private String operateNodeName;

    private String operateUserCode;

    private String operateUserName;

    private long operateTime;

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public long getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(long flightDate) {
        this.flightDate = flightDate;
    }

    public String getBeginNodeCode() {
        return beginNodeCode;
    }

    public void setBeginNodeCode(String beginNodeCode) {
        this.beginNodeCode = beginNodeCode;
    }

    public String getEndNodeCode() {
        return endNodeCode;
    }

    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public int getDepartCargoType() {
        return departCargoType;
    }

    public void setDepartCargoType(int departCargoType) {
        this.departCargoType = departCargoType;
    }

    public int getDepartCargoAmount() {
        return departCargoAmount;
    }

    public void setDepartCargoAmount(int departCargoAmount) {
        this.departCargoAmount = departCargoAmount;
    }

    public double getDepartCargoRealWeight() {
        return departCargoRealWeight;
    }

    public void setDepartCargoRealWeight(double departCargoRealWeight) {
        this.departCargoRealWeight = departCargoRealWeight;
    }

    public double getDepartCargoChargedWeight() {
        return departCargoChargedWeight;
    }

    public void setDepartCargoChargedWeight(double departCargoChargedWeight) {
        this.departCargoChargedWeight = departCargoChargedWeight;
    }

    public String getDepartRemark() {
        return departRemark;
    }

    public void setDepartRemark(String departRemark) {
        this.departRemark = departRemark;
    }

    public List<String> getBatchCodes() {
        return batchCodes;
    }

    public void setBatchCodes(List<String> batchCodes) {
        this.batchCodes = batchCodes;
    }

    public String getOperateNodeCode() {
        return operateNodeCode;
    }

    public void setOperateNodeCode(String operateNodeCode) {
        this.operateNodeCode = operateNodeCode;
    }

    public String getOperateNodeName() {
        return operateNodeName;
    }

    public void setOperateNodeName(String operateNodeName) {
        this.operateNodeName = operateNodeName;
    }

    public String getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(String operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(long operateTime) {
        this.operateTime = operateTime;
    }
}
