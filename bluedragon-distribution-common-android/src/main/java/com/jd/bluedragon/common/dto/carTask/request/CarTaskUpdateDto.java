package com.jd.bluedragon.common.dto.carTask.request;

import java.io.Serializable;
import java.util.Date;

/**
 * 运输车辆任务更新
 */
public class CarTaskUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 路由线路编码
     */
    private String routeLineCode;

    /**
     * 始发网点编码
     */
    private String beginNodeCode;

    /**
     * 始发网点类型
     */
    private String startNodeType;
    /**
     * 目的网点编码
     */
    private String endNodeCode;

    /**
     * 目的网点类型
     */
    private String endNodeType;

    /**
     * 计划发车时间
     */
    private Date planDepartTime;

    /**
     * 线路方量
     */
    private Double volume;

    /**
     * 账号编码
     */
    private String accountCode;

    /**
     * 账号名称
     */
    private String accountName;

    public String getRouteLineCode() {
        return routeLineCode;
    }

    public void setRouteLineCode(String routeLineCode) {
        this.routeLineCode = routeLineCode;
    }

    public String getBeginNodeCode() {
        return beginNodeCode;
    }

    public void setBeginNodeCode(String beginNodeCode) {
        this.beginNodeCode = beginNodeCode;
    }

    public String getStartNodeType() {
        return startNodeType;
    }

    public void setStartNodeType(String startNodeType) {
        this.startNodeType = startNodeType;
    }

    public String getEndNodeCode() {
        return endNodeCode;
    }

    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public String getEndNodeType() {
        return endNodeType;
    }

    public void setEndNodeType(String endNodeType) {
        this.endNodeType = endNodeType;
    }

    public Date getPlanDepartTime() {
        return planDepartTime;
    }

    public void setPlanDepartTime(Date planDepartTime) {
        this.planDepartTime = planDepartTime;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
