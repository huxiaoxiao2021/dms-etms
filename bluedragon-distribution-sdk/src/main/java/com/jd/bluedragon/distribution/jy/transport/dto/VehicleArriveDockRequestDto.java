package com.jd.bluedragon.distribution.jy.transport.dto;

import java.io.Serializable;

/**
 * 运输车辆到达靠台验证入参
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-08 20:50:02 周一
 */
public class VehicleArriveDockRequestDto implements Serializable {
    private static final long serialVersionUID = -6322213434034445807L;

    /**
     * 验证字符
     */
    private String validateStr;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 派车单明细编码
     */
    private String transWorkItemCode;

    /**
     * 始发场地编码
     */
    private String beginNodeCode;

    /**
     * 始发场地名称
     */
    private String beginNodeName;

    /**
     * 承运商类型
     */
    private Long carrierType;

    /**
     * 目的地编码
     */
    private String endNodeCode;

    /**
     * 目的地名称
     */
    private String endNodeName;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 用户名称
     */
    private String userName;

    public VehicleArriveDockRequestDto() {
    }

    public String getValidateStr() {
        return validateStr;
    }

    public void setValidateStr(String validateStr) {
        this.validateStr = validateStr;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
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

    public Long getCarrierType() {
        return carrierType;
    }

    public void setCarrierType(Long carrierType) {
        this.carrierType = carrierType;
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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
