package com.jd.bluedragon.distribution.seal.domain;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName TransWorkItemResult
 * @date 2019/4/8
 */
public class TransWorkItemResult {

    /**
     * 类型
     */
    private Integer type;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
