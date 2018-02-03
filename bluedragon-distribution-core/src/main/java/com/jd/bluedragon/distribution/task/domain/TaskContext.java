package com.jd.bluedragon.distribution.task.domain;

import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * 
 * @ClassName: TaskContext
 * @Description: 任务处理上下文信息
 * @author: wuyoude
 * @date: 2018年1月5日 下午2:19:24
 * 
 * @param <E>
 */
public interface TaskContext<E> {
	/**
	 * 用于保存任务数据的key
	 */
	public static final String KEY_DATA_TASK = "task";
	/**
	 * 用于保存任务实体数据的key
	 */
	public static final String KEY_DATA_BODY = "body";
	/**
	 * 获取当前处理的Task信息
	 * @return
	 */
	Task getTask();
	/**
	 * 设置当前处理的Task信息
	 * @param task
	 */
	void setTask(Task task);
	/**
	 * 获取任务Body转换后的实体
	 * @return
	 */
	E getBody();
	/**
	 * 获取任务Body转换后的实体
	 * @return
	 */
	void setBody(E body);
	/**
	 * 获取任务Body转换后的实体
	 * @return
	 */
	<T> T getData(String key);
	/**
	 * 设置上下文数据
	 * @param key
	 * @param val
	 */
	<T> void setData(String key,T val);
	/**
	 * 清除上下文数据
	 */
	void clear();
}
