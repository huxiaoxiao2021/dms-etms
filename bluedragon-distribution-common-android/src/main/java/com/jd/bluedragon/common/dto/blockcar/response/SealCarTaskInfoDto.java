package com.jd.bluedragon.common.dto.blockcar.response;

import java.io.Serializable;

/**
 * SealCarTaskInfoDto
 * 封车任务信息
 * @author jiaowenqiang
 * @date 2019/6/25
 */
public class SealCarTaskInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运输方式
     */
    private Integer transType;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 任务简码
     */
    private String transWorkItemCode;

    /**
     * 线路编码
     */
    private String routeLineCode;

    /**
     * 线路名称
     */
    private String routeLineName;

    /**
     * 批次号
     */
    private String sendCode;

    @Override
    public String toString() {
        return "SealCarTaskInfoDto{" +
                "transType=" + transType +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", transWorkItemCode='" + transWorkItemCode + '\'' +
                ", routeLineCode='" + routeLineCode + '\'' +
                ", routeLineName='" + routeLineName + '\'' +
                ", sendCode='" + sendCode + '\'' +
                '}';
    }

    public Integer getTransType() {
        return transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
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

    public String getRouteLineCode() {
        return routeLineCode;
    }

    public void setRouteLineCode(String routeLineCode) {
        this.routeLineCode = routeLineCode;
    }

    public String getRouteLineName() {
        return routeLineName;
    }

    public void setRouteLineName(String routeLineName) {
        this.routeLineName = routeLineName;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }
}
