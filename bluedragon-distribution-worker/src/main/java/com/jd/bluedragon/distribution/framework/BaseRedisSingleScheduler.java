package com.jd.bluedragon.distribution.framework;

import com.jd.bluedragon.distribution.task.domain.DmsTaskExecutor;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.poi.hssf.record.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseRedisSingleScheduler extends RedisSingleScheduler {
	private Logger log = LoggerFactory.getLogger(BaseRedisSingleScheduler.class);
    /**
     * 任务开关，默认开启
     */
    protected boolean open = true;
	/**
	 * 任务执行器-负责任务执行操作
	 */
	DmsTaskExecutor<T> dmsTaskExecutor;
	
	/**
	 * redis 任务初始化执行逻辑
	 */
	@Override
	public void init() throws Exception {
		//开关开启时才执行操作
    	if(open){
    		super.init();
    	}else{
    		log.warn("task[{}-{}] is not open!",ownSign,taskType);
    	}
	}
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

	/**
	 * @return the open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @param open the open to set
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}
}