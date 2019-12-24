package com.jd.bluedragon.core.redis;

import com.jd.bluedragon.distribution.base.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskModeAgent {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private BaseService baseService;
	
	private TaskMode taskMode;
	
	/**
	 * 获取数据源模式
	 * @return
	 */
    @Deprecated
	public synchronized TaskMode getTaskMode() {
		if (taskMode == null) {
			taskMode = baseService.getTaskMode();
		} 
		return taskMode;
	}

	/**
	 * 判断任务应使用redis模式还是数据库模式
	 * @param task 任务数据
	 * @return 支持返回true,不支持返回false
	 */
	public boolean isRedisTaskModeSupported(TaskModeAware task) {
		boolean isRedisTaskModeSupported = false;
		try {
			//任务本身支持redis
			isRedisTaskModeSupported = task.findTaskMode().equals(TaskMode.REDIS);
		} catch (Exception e) {
			log.error("isTaskModeSupported 失败", e);
		}
		return isRedisTaskModeSupported;
	}

}
