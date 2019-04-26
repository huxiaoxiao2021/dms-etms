package com.jd.bluedragon.distribution.api.response;

/**
 * 根据任务简码查询任务信息返回对象
 * Created by shipeilin on 2018/1/16.
 */
public class TransWorkItemResponse extends NewSealVehicleResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 运输方式
     */
    private Integer transType;

    /**
     * 派车明细简码
     */
    private String transWorkItemCode;

    /**
     * 调度类型
     */
    private Integer scheduleType;

    /**
     * 运输计划编码
     */
    private String transPlanCode;

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

    public TransWorkItemResponse() {

    }

    public TransWorkItemResponse(Integer code, String message) {
        super(code, message);
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getTransType() {
        return transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public Integer getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(Integer scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getTransPlanCode() {
        return transPlanCode;
    }

    public void setTransPlanCode(String transPlanCode) {
        this.transPlanCode = transPlanCode;
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
