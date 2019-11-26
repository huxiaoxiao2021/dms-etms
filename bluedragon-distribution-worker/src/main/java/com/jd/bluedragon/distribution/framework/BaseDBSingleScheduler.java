package com.jd.bluedragon.distribution.framework;

import com.jd.bluedragon.distribution.task.domain.DmsTaskExecutor;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDBSingleScheduler<T> extends DBSingleScheduler {
	private static Logger log = LoggerFactory.getLogger(BaseDBSingleScheduler.class);
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
			log.error("[{}]处理任务失败[taskId={}]异常信息为：",taskType,task.getId(), e);
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