package com.jd.bluedragon.distribution.task.domain;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.task.api.TaskExecutor;
import org.apache.log4j.Logger;


/**
 *
 * @ClassName: DmsTaskExecutor
 * @Description: Dms任务执行器接口-负责任务执行
 * @author: wuyoude
 * @date: 2018年1月12日 上午12:42:09
 *
 * @param <T>
 */
public abstract class DmsTaskExecutor<T> implements TaskExecutor<Task>{

	private Logger log = Logger.getLogger(DmsTaskExecutor.class);

	/**
	 * 将Task对象转换为实际需要的实体
	 * @param task
	 * @param ownSign
	 * @return
	 */
	public abstract T parse(Task task, String ownSign);
	/**
	 * 执行任务处理
	 * @param taskContext
	 * @param ownSign
	 * @return
	 */
	public abstract boolean execute(TaskContext<T> taskContext, String ownSign);
	/**
	 * 把任务转换为TaskContext并执行
	 * @param task
	 * @param ownSign
	 * @return
	 */
	@Override
	public boolean execute(Task task, String ownSign){
        T body = parse(task, ownSign);
        if (body == null) {
            log.warn(String.format("[任务执行失败]将Task对象转换为实际需要的实体失败，taskBody:%s", JsonHelper.toJson(task)));
            return true;
        }
        TaskContext<T> taskContext = new DmsTaskContext<>();
        taskContext.setTask(task);
        taskContext.setBody(body);
        return execute(taskContext, ownSign);
	}

}
