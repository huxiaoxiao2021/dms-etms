package com.jd.bluedragon.distribution.task.api;


/**
 * 
 * @ClassName: TaskExecutor
 * @Description: 任务执行器接口-负责任务执行
 * @author: wuyoude
 * @date: 2017年10月20日 下午5:47:58
 * 
 * @param <T> 任务实体
 */
public interface TaskExecutor<T> {
	/**
	 * 执行任务处理
	 * @param task
	 * @param ownSign
	 * @return
	 */
	public boolean execute(T t, String ownSign);
}
