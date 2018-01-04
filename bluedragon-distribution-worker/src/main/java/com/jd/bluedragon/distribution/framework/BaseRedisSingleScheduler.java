package com.jd.bluedragon.distribution.framework;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.record.formula.functions.T;

import com.jd.bluedragon.distribution.task.domain.DmsTaskExecutor;
import com.jd.bluedragon.distribution.task.domain.Task;

public class BaseRedisSingleScheduler extends RedisSingleScheduler {
	private Logger logger = Logger.getLogger(BaseRedisSingleScheduler.class);
	/**
	 * 任务执行器-负责任务执行操作
	 */
	DmsTaskExecutor<T> dmsTaskExecutor;

	@Override
	protected boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		try {
			return dmsTaskExecutor.execute(task,ownSign);
		} catch (Exception e) {
			logger.error("["+taskType+"]处理任务失败[taskId=" + task.getId() + "]异常信息为："
							+ e.getMessage(), e);
			return false;
		}
	}

	/**
	 * @return the dmsTaskExecutor
	 */
	public DmsTaskExecutor<T> getDmsTaskExecutor() {
		return dmsTaskExecutor;
	}

	/**
	 * @param dmsTaskExecutor the dmsTaskExecutor to set
	 */
	public void setDmsTaskExecutor(DmsTaskExecutor<T> dmsTaskExecutor) {
		this.dmsTaskExecutor = dmsTaskExecutor;
	}
}