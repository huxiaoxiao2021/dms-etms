package com.jd.bluedragon.distribution.jy.dto.work;

import java.io.Serializable;

/**
 * @ClassName: JyWorkGridManagerCase
 * @Description: 巡检任务表-检查项-实体类
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
public class JyWorkGridManagerCaseQuery implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 业务主键
	 */
	private String bizId;
	
	/**
	 * 任务类型 
	 */
	private Integer taskType;
	/**
	 * 任务编码
	 */
	private String taskCode;	

	/**
	 * 场景编码
	 */
	private String caseCode;
	
    /**
     * 场地编码
     */
    private Integer siteCode;

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public String getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public String getCaseCode() {
		return caseCode;
	}

	public void setCaseCode(String caseCode) {
		this.caseCode = caseCode;
	}

	public Integer getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

}
