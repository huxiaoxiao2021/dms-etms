package com.jd.bluedragon.core.redis;

public interface TaskModeAware {
	
	/**
	 * 获取任务是否支持Redis模式
	 * @return
	 */
	TaskMode findTaskMode();
	
	/**
	 * 获取对应的任务类型
	 * @return
	 */
	QueueKeyInfo findQueueKey();
}
