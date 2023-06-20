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
public class TaskWorkGridManagerSiteScanData implements Serializable {

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
	 * 执行次数，首次执行需要初始化网格任务数据
	 */
	private int executeCount = 0;
	/**
	 * 记录上次任务执行日期:yyyy-mm-dd格式
	 */
	private String lastExecuteDay;
	
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
	public int getExecuteCount() {
		return executeCount;
	}
	public void setExecuteCount(int executeCount) {
		this.executeCount = executeCount;
	}
	public String getLastExecuteDay() {
		return lastExecuteDay;
	}
	public void setLastExecuteDay(String lastExecuteDay) {
		this.lastExecuteDay = lastExecuteDay;
	}
}
