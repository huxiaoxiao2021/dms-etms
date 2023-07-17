package com.jd.bluedragon.distribution.jy.dto.work;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: TaskWorkGridManagerAutoCloseData
 * @Description: 主任务：巡检任务-自动关闭-实体类
 * @author wuyoude
 * @date 2023年06月15日 00:55:07
 *
 */
public class TaskWorkGridManagerAutoCloseData implements Serializable {

	private static final long serialVersionUID = 1L;

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
	private Date executeTime;
	/**
	 * 本次需要关闭的页面主键列表
	 */
	private List<String> bizIdList;
	
	public String getTaskConfigCode() {
		return taskConfigCode;
	}
	public void setTaskConfigCode(String taskConfigCode) {
		this.taskConfigCode = taskConfigCode;
	}
	public String getTaskBatchCode() {
		return taskBatchCode;
	}
	public void setTaskBatchCode(String taskBatchCode) {
		this.taskBatchCode = taskBatchCode;
	}
	public Integer getSiteCode() {
		return siteCode;
	}
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}
	public Date getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}
	public List<String> getBizIdList() {
		return bizIdList;
	}
	public void setBizIdList(List<String> bizIdList) {
		this.bizIdList = bizIdList;
	}
}
