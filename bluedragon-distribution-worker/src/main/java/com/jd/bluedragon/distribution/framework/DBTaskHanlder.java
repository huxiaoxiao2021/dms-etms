package com.jd.bluedragon.distribution.framework;

import java.util.Comparator;

import org.apache.log4j.Logger;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.tbschedule.redis.template.TaskEntry;


public class DBTaskHanlder extends TaskHanlder {

	protected final Logger logger = Logger.getLogger(this.getClass());

	public boolean preHandle(Task task) {
		return getTaskService().doLock(task);
	}

	public boolean handleSuccess(Task task) {
		return getTaskService().doDone(task);
	}

	public boolean handleError(Task task) {
		return getTaskService().doError(task);
	}

	public boolean handleRedo(Task task){
		return getTaskService().doRepeat(task);
	}

	public Comparator<TaskEntry<Task>> getComparator() {
		return null;
	}

	public TaskService getTaskService() {
		return (TaskService) SpringHelper.getBean("taskService");
	}
}
