package com.jd.bluedragon.distribution.task.asynBuffer.tbschedule;

import java.util.List;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.ql.framework.asynBuffer.producer.tbchedule.TbscheduleTaskStore;

/**
 * dms系统中的淘宝调度任务存储器。
 * @author yangwubing
 *
 */
public class DmsTbscheduleTaskStore implements TbscheduleTaskStore<Task> {
	
	private TaskService taskService;

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	@Override
	public void save(Task task) {
		if(task==null){
			return;
		}
		taskService.add(task, true);
	}

	@Override
	public void save(List<Task> tasks) {
		if(tasks==null || tasks.size()==0){
			return;
		}
		
		for(Task task:tasks){
			taskService.add(task, true);
		}
	}

}
