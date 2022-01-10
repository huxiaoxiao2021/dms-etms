package com.jd.bluedragon.distribution.framework;

import java.util.Comparator;

import org.apache.log4j.Logger;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.tbschedule.redis.template.TaskEntry;


public class NoneTaskHanlder extends TaskHanlder {

	protected final Logger logger = Logger.getLogger(this.getClass());

	public boolean preHandle(Task task) {
		return true;
	}

	public boolean handleSuccess(Task task) {
		return true;
	}

	public boolean handleError(Task task) {
		return true;
	}

	public boolean handleRedo(Task task){
		return true;
	}

	public Comparator<TaskEntry<Task>> getComparator() {
		return null;
	}
}
