package com.jd.bluedragon.distribution.jy.dto.work;

import java.util.Date;
import java.io.Serializable;

/**
 * @ClassName: TaskWorkGridManagerSiteScan
 * @Description: 子任务：巡检任务-场地扫描-实体类
 * @author wuyoude
 * @date 2023年06月15日 00:55:07
 *
 */
public class TaskWorkGridManagerSiteScan implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Long taskId;

	/**
	 * 
	 */
	private Date createTime;

	/**
	 * 
	 */
	private Date updateTime;

	/**
	 * 
	 */
	private String keyword1;

	/**
	 * 
	 */
	private String keyword2;

	/**
	 * 
	 */
	private Double createSiteCode;

	/**
	 * 
	 */
	private Double receiveSiteCode;

	/**
	 * 任务配置编码
	 */
	private String taskConfigCode;

	/**
	 * 任务批次号
	 */
	private String taskBatchCode;

	/**
	 * 站点编号
	 */
	private Integer siteCode;

	/**
	 * 
	 */
	private String body;

	/**
	 * 
	 */
	private Double executeCount;

	/**
	 * 
	 */
	private Double taskType;

	/**
	 * 
	 */
	private Double taskStatus;

	/**
	 * 
	 */
	private Double yn;

	/**
	 * 
	 */
	private String ownSign;

	/**
	 * 
	 */
	private String fingerprint;

	/**
	 * 
	 */
	private Date executeTime;

	/**
	 * 任务队列号
	 */
	private Integer queueId;

	/**
	 *
	 * @param taskId
	 */
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	/**
	 *
	 * @return taskId
	 */
	public Long getTaskId() {
		return this.taskId;
	}

	/**
	 *
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 *
	 * @return createTime
	 */
	public Date getCreateTime() {
		return this.createTime;
	}

	/**
	 *
	 * @param updateTime
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 *
	 * @return updateTime
	 */
	public Date getUpdateTime() {
		return this.updateTime;
	}

	/**
	 *
	 * @param keyword1
	 */
	public void setKeyword1(String keyword1) {
		this.keyword1 = keyword1;
	}

	/**
	 *
	 * @return keyword1
	 */
	public String getKeyword1() {
		return this.keyword1;
	}

	/**
	 *
	 * @param keyword2
	 */
	public void setKeyword2(String keyword2) {
		this.keyword2 = keyword2;
	}

	/**
	 *
	 * @return keyword2
	 */
	public String getKeyword2() {
		return this.keyword2;
	}

	/**
	 *
	 * @param createSiteCode
	 */
	public void setCreateSiteCode(Double createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	/**
	 *
	 * @return createSiteCode
	 */
	public Double getCreateSiteCode() {
		return this.createSiteCode;
	}

	/**
	 *
	 * @param receiveSiteCode
	 */
	public void setReceiveSiteCode(Double receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	/**
	 *
	 * @return receiveSiteCode
	 */
	public Double getReceiveSiteCode() {
		return this.receiveSiteCode;
	}

	/**
	 *
	 * @param taskConfigCode
	 */
	public void setTaskConfigCode(String taskConfigCode) {
		this.taskConfigCode = taskConfigCode;
	}

	/**
	 *
	 * @return taskConfigCode
	 */
	public String getTaskConfigCode() {
		return this.taskConfigCode;
	}

	/**
	 *
	 * @param taskBatchCode
	 */
	public void setTaskBatchCode(String taskBatchCode) {
		this.taskBatchCode = taskBatchCode;
	}

	/**
	 *
	 * @return taskBatchCode
	 */
	public String getTaskBatchCode() {
		return this.taskBatchCode;
	}

	/**
	 *
	 * @param siteCode
	 */
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	/**
	 *
	 * @return siteCode
	 */
	public Integer getSiteCode() {
		return this.siteCode;
	}

	/**
	 *
	 * @param body
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 *
	 * @return body
	 */
	public String getBody() {
		return this.body;
	}

	/**
	 *
	 * @param executeCount
	 */
	public void setExecuteCount(Double executeCount) {
		this.executeCount = executeCount;
	}

	/**
	 *
	 * @return executeCount
	 */
	public Double getExecuteCount() {
		return this.executeCount;
	}

	/**
	 *
	 * @param taskType
	 */
	public void setTaskType(Double taskType) {
		this.taskType = taskType;
	}

	/**
	 *
	 * @return taskType
	 */
	public Double getTaskType() {
		return this.taskType;
	}

	/**
	 *
	 * @param taskStatus
	 */
	public void setTaskStatus(Double taskStatus) {
		this.taskStatus = taskStatus;
	}

	/**
	 *
	 * @return taskStatus
	 */
	public Double getTaskStatus() {
		return this.taskStatus;
	}

	/**
	 *
	 * @param yn
	 */
	public void setYn(Double yn) {
		this.yn = yn;
	}

	/**
	 *
	 * @return yn
	 */
	public Double getYn() {
		return this.yn;
	}

	/**
	 *
	 * @param ownSign
	 */
	public void setOwnSign(String ownSign) {
		this.ownSign = ownSign;
	}

	/**
	 *
	 * @return ownSign
	 */
	public String getOwnSign() {
		return this.ownSign;
	}

	/**
	 *
	 * @param fingerprint
	 */
	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	/**
	 *
	 * @return fingerprint
	 */
	public String getFingerprint() {
		return this.fingerprint;
	}

	/**
	 *
	 * @param executeTime
	 */
	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	/**
	 *
	 * @return executeTime
	 */
	public Date getExecuteTime() {
		return this.executeTime;
	}

	/**
	 *
	 * @param queueId
	 */
	public void setQueueId(Integer queueId) {
		this.queueId = queueId;
	}

	/**
	 *
	 * @return queueId
	 */
	public Integer getQueueId() {
		return this.queueId;
	}


}
