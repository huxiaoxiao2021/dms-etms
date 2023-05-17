package com.jd.bluedragon.distribution.jy.strand;

import java.io.Serializable;
import java.util.Date;

/**
 * 拣运-滞留上报任务数据库实体
 *
 * @author hujiping
 * @date 2023/3/28 3:49 PM
 */
public class JyBizTaskStrandReportEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 运输驳回唯一键
     */
    private String transportRejectBiz;
    
    /**
     * 场地编码
     */
    private Integer siteCode;

    /**
     * 场地名称
     */
    private String siteName;

    /**
     * 滞留原因编码
     */
    private Integer strandCode;

    /**
     * 滞留原因
     */
    private String strandReason;

    /**
     * 下级流向场地编码
     */
    private Integer nextSiteCode;

    /**
     * 下级流向场地名称
     */
    private String nextSiteName;
    
    /**
     * 波次时间
     */
    private String waveTime;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    /**
     * 创建人ERP
     */
    private String createUserErp;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 更新人ERP
     */
    private String updateUserErp;

    /**
     * 提交人ERP
     */
    private String submitUserErp;
    
    /**
     * 任务预计关闭时间
     */
    private Date expectCloseTime;

    /**
     * 提交时间
     */
    private Date submitTime;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 逻辑删除标志,0-删除,1-正常
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

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getTransportRejectBiz() {
        return transportRejectBiz;
    }

    public void setTransportRejectBiz(String transportRejectBiz) {
        this.transportRejectBiz = transportRejectBiz;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getStrandCode() {
        return strandCode;
    }

    public void setStrandCode(Integer strandCode) {
        this.strandCode = strandCode;
    }

    public String getStrandReason() {
        return strandReason;
    }

    public void setStrandReason(String strandReason) {
        this.strandReason = strandReason;
    }

    public Integer getNextSiteCode() {
        return nextSiteCode;
    }

    public void setNextSiteCode(Integer nextSiteCode) {
        this.nextSiteCode = nextSiteCode;
    }

    public String getNextSiteName() {
        return nextSiteName;
    }

    public void setNextSiteName(String nextSiteName) {
        this.nextSiteName = nextSiteName;
    }

    public String getWaveTime() {
        return waveTime;
    }

    public void setWaveTime(String waveTime) {
        this.waveTime = waveTime;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getSubmitUserErp() {
        return submitUserErp;
    }

    public void setSubmitUserErp(String submitUserErp) {
        this.submitUserErp = submitUserErp;
    }

    public Date getExpectCloseTime() {
        return expectCloseTime;
    }

    public void setExpectCloseTime(Date expectCloseTime) {
        this.expectCloseTime = expectCloseTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
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
