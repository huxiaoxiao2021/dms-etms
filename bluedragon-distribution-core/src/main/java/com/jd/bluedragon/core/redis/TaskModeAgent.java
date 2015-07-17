package com.jd.bluedragon.core.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.distribution.base.service.BaseService;

@Component
public class TaskModeAgent {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private BaseService baseService;
	
	private TaskMode taskMode;
	
	/**
	 * 获取数据源模式
	 * @return
	 */
	public synchronized TaskMode getTaskMode() {
		if (taskMode == null) {
			taskMode = baseService.getTaskMode();
		} 
		return taskMode;
	}

	/**
	 * 判断任务应使用redis模式还是数据库模式, 根据当前数据源模式及任务是否支持redis模式来判断
	 * @param task 任务数据
	 * @return 支持返回true,不支持返回false
	 */
	public boolean isRedisTaskModeSupported(TaskModeAware task) {
		boolean isRedisTaskModeSupported = false;
		try {
			//任务本身支持redis,并且现在的数据源模式为redis
			isRedisTaskModeSupported = task.findTaskMode().equals(TaskMode.REDIS)
					&& getTaskMode().equals(TaskMode.REDIS);
		} catch (Exception e) {
			logger.error("isTaskModeSupported 失败", e);
		}
		return isRedisTaskModeSupported;
	}

}
