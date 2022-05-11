package com.jd.bluedragon.distribution.board.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务和组板关系
 * @author lvyuan21
 * @date 2020-12-25 16:19
 */
public class TaskBoard implements Serializable {

    private static final long serialVersionUID = -7623509285189482980L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 业务节点：卸车组板-50
     */
    private Integer businessId;

    /**
     * 板号
     */
    private String boardCode;

    /**
     * 当前转运中心Id
     */
    private Integer currentSiteCode;

    /**
     * 当前转运中心名称
     */
    private String currentSiteName;

    /**
     * 当前转运中心code
     */
    private String currentDmsCode;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 机构编码
     */
    private Integer orgCode;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 创建人erp
     */
    private String createUserErp;

    /**
     * 更新人名称
     */
    private String updateUserName;

    /**
     * 更新人Erp
     */
    private String updateUserErp;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 可用状态: 1 可用, 0 不可用
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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getCurrentSiteCode() {
        return currentSiteCode;
    }

    public void setCurrentSiteCode(Integer currentSiteCode) {
        this.currentSiteCode = currentSiteCode;
    }

    public String getCurrentSiteName() {
        return currentSiteName;
    }

    public void setCurrentSiteName(String currentSiteName) {
        this.currentSiteName = currentSiteName;
    }

    public String getCurrentDmsCode() {
        return currentDmsCode;
    }

    public void setCurrentDmsCode(String currentDmsCode) {
        this.currentDmsCode = currentDmsCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
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
