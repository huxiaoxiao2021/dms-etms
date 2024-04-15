package com.jd.bluedragon.distribution.router.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 动态线路替换方案
 * @author fanggang7
 * @date 2024-04-01 09:43:26 周一
 */
public class DynamicLineReplacePlanMq implements Serializable {

    private static final long serialVersionUID = -4544720569089863907L;
    /**
     * 替换方案标识
     */
    private Long versionId;

    /**d
     * 当前站点
     */
    private String startNodeCode;

    /**
     * 原计划线路目的点
     */
    private String oldEndNodeCode;

    /**
     * 新计划线路目的点
     */
    private String newEndNodeCode;

    /**
     * 替换生效时间
     */
    private Date enableTime;

    /**
     * 替换失效时间
     */
    private Date disableTime;

    /**
     * 原计划线路编码
     */
    private String oldPlanLineCode;

    /**
     * 原计划线路发出时间
     */
    private Date oldPlanDepartureTime;

    /**
     * 原计划线路配载
     */
    private String oldPlanFlowCode;

    /**
     * 新计划线路编码
     */
    private String newPlanLineCode;

    /**
     * 新计划线路发出时间
     */
    private Date newPlanDepartureTime;

    /**
     * 新计划线路配载
     */
    private String newPlanFlowCode;

    /**
     * 推送时间
     */
    private Long pushTime;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getStartNodeCode() {
        return startNodeCode;
    }

    public void setStartNodeCode(String startNodeCode) {
        this.startNodeCode = startNodeCode;
    }

    public String getOldEndNodeCode() {
        return oldEndNodeCode;
    }

    public void setOldEndNodeCode(String oldEndNodeCode) {
        this.oldEndNodeCode = oldEndNodeCode;
    }

    public String getNewEndNodeCode() {
        return newEndNodeCode;
    }

    public void setNewEndNodeCode(String newEndNodeCode) {
        this.newEndNodeCode = newEndNodeCode;
    }

    public Date getEnableTime() {
        return enableTime;
    }

    public void setEnableTime(Date enableTime) {
        this.enableTime = enableTime;
    }

    public Date getDisableTime() {
        return disableTime;
    }

    public void setDisableTime(Date disableTime) {
        this.disableTime = disableTime;
    }

    public String getOldPlanLineCode() {
        return oldPlanLineCode;
    }

    public void setOldPlanLineCode(String oldPlanLineCode) {
        this.oldPlanLineCode = oldPlanLineCode;
    }

    public Date getOldPlanDepartureTime() {
        return oldPlanDepartureTime;
    }

    public void setOldPlanDepartureTime(Date oldPlanDepartureTime) {
        this.oldPlanDepartureTime = oldPlanDepartureTime;
    }

    public String getOldPlanFlowCode() {
        return oldPlanFlowCode;
    }

    public void setOldPlanFlowCode(String oldPlanFlowCode) {
        this.oldPlanFlowCode = oldPlanFlowCode;
    }

    public String getNewPlanLineCode() {
        return newPlanLineCode;
    }

    public void setNewPlanLineCode(String newPlanLineCode) {
        this.newPlanLineCode = newPlanLineCode;
    }

    public Date getNewPlanDepartureTime() {
        return newPlanDepartureTime;
    }

    public void setNewPlanDepartureTime(Date newPlanDepartureTime) {
        this.newPlanDepartureTime = newPlanDepartureTime;
    }

    public String getNewPlanFlowCode() {
        return newPlanFlowCode;
    }

    public void setNewPlanFlowCode(String newPlanFlowCode) {
        this.newPlanFlowCode = newPlanFlowCode;
    }

    public Long getPushTime() {
        return pushTime;
    }

    public void setPushTime(Long pushTime) {
        this.pushTime = pushTime;
    }
}
