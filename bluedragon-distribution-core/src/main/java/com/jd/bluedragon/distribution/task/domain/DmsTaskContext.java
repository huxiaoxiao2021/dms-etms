package com.jd.bluedragon.distribution.task.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: TaskContext
 * @Description: 任务处理上下文信息
 * @author: wuyoude
 * @date: 2018年1月5日 下午2:19:24
 * 
 * @param <E>
 */
public class DmsTaskContext<E> implements TaskContext<E>{
	/**
	 * 保存任务上下文信息
	 */
	private Map<String,Object> context;
	
	public DmsTaskContext() {
		super();
		this.context = new HashMap<String,Object>(8);
	}
	/**
	 * 获取当前处理的Task
	 * @return
	 */
	public Task getTask(){
		return getData(KEY_DATA_TASK);
	}
	/**
	 * 设置当前处理的任务
	 */
	public void setTask(Task task){
		setData(KEY_DATA_TASK,task);
	}
	/**
	 * 获取任务Body转换后的实体
	 * @return
	 */
	public E getBody(){
		return getData(KEY_DATA_BODY);
	}
	/**
	 * 设置任务Body转换后的实体
	 */
	public void setBody(E bodyData){
		setData(KEY_DATA_BODY,bodyData);
	}
	/**
	 * 获取任务上下文数据
	 */
	public <T> T getData(String key){
		return (T)context.get(key);
	}
	/**
	 * 获取任务上下文数据
	 * @return
	 */
	public <T> void setData(String key,T val){
		context.put(key, val);
	}
	/**
	 * 清除上下文数据
	 */
	public void clear(){
		context.clear();
	}
}
