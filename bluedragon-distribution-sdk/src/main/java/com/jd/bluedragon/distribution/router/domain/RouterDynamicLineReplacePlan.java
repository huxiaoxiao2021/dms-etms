package com.jd.bluedragon.distribution.router.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2023-11-12 14:09:38 周日
 */
public class RouterDynamicLineReplacePlan implements Serializable{
    private static final long serialVersionUID = 5454155825314635342L;

    //columns START
    /**
     * 主键  db_column: id
     */
    private Long id;
    /**
     * 版本ID  db_column: version_id
     */
    private Long versionId;
    /**
     * 当前场地id  db_column: start_site_id
     */
    private Integer startSiteId;
    /**
     * 当前场地id  db_column: start_site_name
     */
    private String startSiteName;
    /**
     * 原计划线路目的点ID  db_column: old_end_site_id
     */
    private Integer oldEndSiteId;
    /**
     * 原计划线路目的点名称  db_column: old_end_site_name
     */
    private String oldEndSiteName;
    /**
     * 原线路编码  db_column: old_plan_line_code
     */
    private String oldPlanLineCode;
    /**
     * 原计划线路配载  db_column: old_plan_flow_code
     */
    private String oldPlanFlowCode;
    /**
     * 原计划线路发出时间  db_column: old_plan_departure_time
     */
    private Date oldPlanDepartureTime;
    /**
     * 新计划线路目的点ID  db_column: new_end_site_id
     */
    private Integer newEndSiteId;
    /**
     * 新计划线路目的点名称  db_column: new_end_site_name
     */
    private String newEndSiteName;
    /**
     * 新线路编码  db_column: new_plan_line_code
     */
    private String newPlanLineCode;
    /**
     * 新计划线路配载  db_column: new_plan_flow_code
     */
    private String newPlanFlowCode;
    /**
     * 新计划线路发出时间  db_column: new_plan_departure_time
     */
    private Date newPlanDepartureTime;
    /**
     * 替换线路生效时间  db_column: enable_time
     */
    private Date enableTime;
    /**
     * 替换线路生效时间  db_column: disable_time
     */
    private Date disableTime;
    /**
     * 路由推送时间  db_column: push_time
     */
    private Long pushTime;
    /**
     * 启用状态  db_column: enable_status
     */
    private Integer enableStatus;
    /**
     * 创建时间  db_column: create_time
     */
    private Date createTime;
    /**
     * 修改时间  db_column: update_time
     */
    private Date updateTime;
    /**
     * 修改人ID  db_column: update_user_id
     */
    private Integer updateUserId;
    /**
     * 修改人  db_column: update_user_code
     */
    private String updateUserCode;
    /**
     * 修改人名称  db_column: update_user_name
     */
    private String updateUserName;
    /**
     * 有效标志  db_column: yn
     */
    private Integer yn;
    /**
     * 数据库时间  db_column: ts
     */
    private Date ts;
    //columns END

    public RouterDynamicLineReplacePlan(){
    }
    public RouterDynamicLineReplacePlan(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Integer getOldEndSiteId() {
        return oldEndSiteId;
    }

    public void setOldEndSiteId(Integer oldEndSiteId) {
        this.oldEndSiteId = oldEndSiteId;
    }

    public String getOldEndSiteName() {
        return oldEndSiteName;
    }

    public void setOldEndSiteName(String oldEndSiteName) {
        this.oldEndSiteName = oldEndSiteName;
    }

    public String getOldPlanLineCode() {
        return oldPlanLineCode;
    }

    public void setOldPlanLineCode(String oldPlanLineCode) {
        this.oldPlanLineCode = oldPlanLineCode;
    }

    public String getOldPlanFlowCode() {
        return oldPlanFlowCode;
    }

    public void setOldPlanFlowCode(String oldPlanFlowCode) {
        this.oldPlanFlowCode = oldPlanFlowCode;
    }

    public Date getOldPlanDepartureTime() {
        return oldPlanDepartureTime;
    }

    public void setOldPlanDepartureTime(Date oldPlanDepartureTime) {
        this.oldPlanDepartureTime = oldPlanDepartureTime;
    }

    public Integer getNewEndSiteId() {
        return newEndSiteId;
    }

    public void setNewEndSiteId(Integer newEndSiteId) {
        this.newEndSiteId = newEndSiteId;
    }

    public String getNewEndSiteName() {
        return newEndSiteName;
    }

    public void setNewEndSiteName(String newEndSiteName) {
        this.newEndSiteName = newEndSiteName;
    }

    public String getNewPlanLineCode() {
        return newPlanLineCode;
    }

    public void setNewPlanLineCode(String newPlanLineCode) {
        this.newPlanLineCode = newPlanLineCode;
    }

    public String getNewPlanFlowCode() {
        return newPlanFlowCode;
    }

    public void setNewPlanFlowCode(String newPlanFlowCode) {
        this.newPlanFlowCode = newPlanFlowCode;
    }

    public Date getNewPlanDepartureTime() {
        return newPlanDepartureTime;
    }

    public void setNewPlanDepartureTime(Date newPlanDepartureTime) {
        this.newPlanDepartureTime = newPlanDepartureTime;
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

    public Long getPushTime() {
        return pushTime;
    }

    public void setPushTime(Long pushTime) {
        this.pushTime = pushTime;
    }

    public Integer getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(Integer enableStatus) {
        this.enableStatus = enableStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Integer updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(String updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
