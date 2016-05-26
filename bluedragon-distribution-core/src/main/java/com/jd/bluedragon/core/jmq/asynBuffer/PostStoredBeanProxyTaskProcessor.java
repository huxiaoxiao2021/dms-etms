package com.jd.bluedragon.core.jmq.asynBuffer;

import java.util.List;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.ql.dcam.config.ConfigManager;
import com.jd.ql.framework.asynBuffer.comsumer.BeanProxyTaskProcessor;


/**
 * 支持数据处理成功后的处理的任务处理器，成功后需要将任务存储到task表中，供错误数据查询及重置修复用。
 * @author yangwubing
 *
 */
public class PostStoredBeanProxyTaskProcessor extends BeanProxyTaskProcessor<Task> {

	public final static String STOER_TASK_ENBALED_KEY = "asynBuffer.jmqComsumer.task.processor.post.task.store.enbaled";
	
	private ConfigManager configManager;
	
	private TaskService taskService;
	
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}
	
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}


	/**
	 * 是否存储消费成功的任务信息。
	 * @return
	 */
	public boolean isStoreSucessTask(){
		String enabled = configManager.getProperty(STOER_TASK_ENBALED_KEY);
		if(enabled==null || enabled.length()==0){
			return true;
		}
		return Boolean.parseBoolean(enabled);
	}

	public PostStoredBeanProxyTaskProcessor(Object target, String methodName) {
		super(target, methodName);
	}

	@Override
	public boolean process(List<Task> tasks) {
		boolean result =  super.process(tasks);
		
		//若处理成功，并且开关开启了。
		if(result && isStoreSucessTask()){
			return saveTask(tasks);
		}
		return result;
	}

	@Override
	public boolean process(Task task) {
		boolean result = super.process(task);
		//若处理成功，并且开关开启了。
		if(result && isStoreSucessTask()){
			return saveTask(task);
		}
		return result;
	}
	
	
	protected boolean saveTask(Task task) {
		task.setStatus(Task.TASK_STATUS_FINISHED);
        task.setExecuteCount(0);
        List<Task> taskList = taskService.findTasksByFingerprint(task);
        //如果任务表已经存在数据，则去重，直接返回成功
        if(null != taskList && taskList.size() > 0) {
            return true;
        }
		int r = taskService.doAddWithStatus(task);
		if(r>0){
			return true;
		}
		return false;
	}

	protected boolean saveTask(List<Task> tasks) {
		for(Task task:tasks){
			this.saveTask(task);
		}
		return true;
	}

}
