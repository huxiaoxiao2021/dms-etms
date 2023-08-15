package com.jd.bluedragon.distribution.jy.dto.work;

import java.util.Date;
import java.io.Serializable;

/**
 * @ClassName: TaskWorkGridManagerScan
 * @Description: 主任务：巡检任务-扫描-实体类
 * @author wuyoude
 * @date 2023年06月15日 00:55:07
 *
 */
public class TaskWorkGridManagerScanData implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 任务配置编码
	 */
	private String taskConfigCode;

	/**
	 * 
	 */
	private Date executeTime;

	public String getTaskConfigCode() {
		return taskConfigCode;
	}

	public void setTaskConfigCode(String taskConfigCode) {
		this.taskConfigCode = taskConfigCode;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

}
