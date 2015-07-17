package com.jd.bluedragon.distribution.framework;

import java.util.Comparator;

import org.apache.log4j.Logger;

import com.jd.bluedragon.core.redis.MD5Hash;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.tbschedule.redis.template.TaskEntry;

public class RedisTaskHanlder extends TaskHanlder {

	protected final Logger logger = Logger.getLogger(this.getClass());

	protected TaskService taskService;

	public boolean preHandle(Task task) {
		return true;
	}

	public boolean handleSuccess(Task task) {
		task.setStatus(Task.TASK_STATUS_FINISHED);
		task.setExecuteCount(0);
		return taskService.doAddWithStatus(task) > 0 ? true : false;
	}

	@Override
	public boolean handleRedo(Task task) {
		task.setStatus(Task.TASK_STATUS_FAILED);
		task.setExecuteCount(0);
		return taskService.doAddWithStatus(task) > 0 ? true : false;
	}

	public boolean handleError(Task task) {
		task.setStatus(Task.TASK_STATUS_UNHANDLED);
		task.setExecuteCount(0);
		return taskService.doAddWithStatus(task) > 0 ? true : false;
	}

	public Comparator<TaskEntry<Task>> getComparator() {
		return new Comparator<TaskEntry<Task>>() {
			public int compare(TaskEntry<Task> taskEntry1,
					TaskEntry<Task> taskEntry2) {
				try {
					Task task1 = taskEntry1.getTask();
					Task task2 = taskEntry2.getTask();

					String fingerprint1 = task1.getFingerprint();
					String fingerprint2 = task2.getFingerprint();

					if (StringHelper.isNotEmpty(fingerprint1)
							&& StringHelper.isNotEmpty(fingerprint2)) {
						if (fingerprint1.equals(fingerprint2)) {
							return 0;
						}
					} else if (StringHelper.isEmpty(fingerprint1)
							&& StringHelper.isEmpty(fingerprint2)) {
						String o1Md5 = String.valueOf(new MD5Hash().hash(task1
								.getBody().getBytes()));
						String o2Md5 = String.valueOf(new MD5Hash().hash(task2
								.getBody().getBytes()));
						if (StringHelper.isNotEmpty(o1Md5)
								&& StringHelper.isNotEmpty(o2Md5)
								&& o1Md5.equals(o2Md5)) {
							return 0;
						}
					}
					return 1;
				} catch (Exception e) {
					return 1;
				}
			}
		};
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
}
