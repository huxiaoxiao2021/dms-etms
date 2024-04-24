package com.jd.bluedragon.common.dto.router.dynamicLine.response;

import java.io.Serializable;
import java.util.Date;

/**
 * 动态线路替换方案
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-02 10:41:47 周二
 */
public class RouterDynamicLineReplacePlanVo implements Serializable {

    private static final long serialVersionUID = 3759170076330896356L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 版本ID
     */
    private Long versionId;
    /**
     * 当前场地id
     */
    private Integer startSiteId;
    /**
     * 当前场地id
     */
    private String startSiteName;
    /**
     * 原计划线路目的点ID
     */
    private Integer oldEndSiteId;
    /**
     * 原计划线路目的点名称
     */
    private String oldEndSiteName;
    /**
     * 原线路编码
     */
    private String oldPlanLineCode;
    /**
     * 原计划线路配载
     */
    private String oldPlanFlowCode;
    /**
     * 原计划线路发出时间
     */
    private Date oldPlanDepartureTime;
    /**
     * 新计划线路目的点ID
     */
    private Integer newEndSiteId;
    /**
     * 新计划线路目的点名称
     */
    private String newEndSiteName;
    /**
     * 新线路编码
     */
    private String newPlanLineCode;
    /**
     * 新计划线路配载
     */
    private String newPlanFlowCode;
    /**
     * 新计划线路发出时间
     */
    private Date newPlanDepartureTime;
    /**
     * 替换线路生效时间
     */
    private Date enableTime;
    /**
     * 替换线路生效时间
     */
    private Date disableTime;
    /**
     * 路由推送时间
     */
    private Long pushTime;
    /**
     * 启用状态
     */
    private Integer enableStatus;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 修改人ID
     */
    private Integer updateUserId;
    /**
     * 修改人
     */
    private String updateUserCode;
    /**
     * 修改人名称
     */
    private String updateUserName;
    /**
     * 有效标志
     */
    private Integer yn;
    /**
     * 数据库时间
     */
    private Date ts;

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
