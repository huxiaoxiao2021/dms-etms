package com.jd.bluedragon.core.jmq.asynBuffer;

import java.util.List;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.ql.framework.asynBuffer.comsumer.BeanProxyTaskProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.annotation.Resource;


/**
 * 支持数据处理成功后的处理的任务处理器，成功后需要将任务存储到task表中，供错误数据查询及重置修复用。
 * @author yangwubing
 *
 */
public class PostStoredBeanProxyTaskProcessor extends BeanProxyTaskProcessor<Task> {

	private TaskService taskService;

	@Resource
	private UccPropertyConfiguration uccPropertyConfiguration;

	private final Log logger = LogFactory.getLog(this.getClass());

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}


	/**
	 * 是否存储消费成功的任务信息。
	 * @return
	 */
	public boolean isStoreSucessTask(){
		Boolean enabled = uccPropertyConfiguration.getAsynBufferJmqComsumerTaskProcessorPostTaskStoreEnbaled();
		if(enabled==null ){
            enabled = true;
		}
		return enabled;
	}

	public PostStoredBeanProxyTaskProcessor(Object target, String methodName) {
		super(target, methodName);
	}

	@Override
	public boolean process(List<Task> tasks) {
		boolean result =  super.process(tasks);
		//如果消费失败，落库
		if(!result){
			return saveConsumerFailedTask(tasks);
		}else if(isStoreSucessTask()){
			return saveTask(tasks);
		}
		return result;
	}

	@Override
	public boolean process(Task task) {
		boolean result = super.process(task);
		//如果消费失败，落库
		if(!result){
			return saveConsumerFailedTask(task);
		}else if(isStoreSucessTask()){
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

	protected boolean saveConsumerFailedTask(List<Task> tasks) {
		for(Task task:tasks){
			this.saveConsumerFailedTask(task);
		}
		return true;
	}

	protected boolean saveConsumerFailedTask(Task task) {
		logger.info("【异步缓冲组件】消费消息失败，执行落库,消息内容："+task);
		task.setStatus(Task.TASK_STATUS_UNHANDLED);
		task.setExecuteCount(0);
		try {
			taskService.doAddTask(task, false);
		}catch (Exception e){
			logger.error("消费消息失败，落库失败，数据存入System_Log表!"+e.getMessage(), e);
			SystemLog systemLog = new SystemLog();
			systemLog.setKeyword1(task.getKeyword1());
			systemLog.setKeyword2(task.getKeyword2());
			systemLog.setKeyword3(task.getBoxCode());
			systemLog.setKeyword4(Task.TASK_STATUS_PARSE_ERROR.longValue());//表示任务执行失败
			systemLog.setType(task.getType().longValue());
			systemLog.setContent(task.getBody());
			SystemLogUtil.log(systemLog);
		}
		return true;
	}

}
