package com.jd.bluedragon.distribution.framework;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.tbschedule.dto.ScheduleQueue;
import com.jd.tbschedule.redis.template.TaskEntry;

public abstract class LimitRedisSingleScheduler extends RedisSingleScheduler {

	private final static Logger log = Logger
			.getLogger(LimitRedisSingleScheduler.class);

	@Autowired
	protected TaskService taskService;

	private int limitNum = 2000;
	private int keepRedisNum = 1;

	protected List<TaskEntry<Task>> filterTasksFromRedis(
			List<TaskEntry<Task>> tasks, String queueKey,
			Map<String, ScheduleQueue> queueNumberMap) {

		long totalNum = redisClient.llen(queueKey);
		if (totalNum > limitNum && tasks.size() > keepRedisNum) {
			List<TaskEntry<Task>> redisTasks = tasks.subList(0, keepRedisNum);
			List<TaskEntry<Task>> jdbcTasks = tasks.subList(keepRedisNum,
					tasks.size());
			saveDBTasks(jdbcTasks, queueNumberMap);
			return redisTasks;
		} else {
			return tasks;
		}
	}

	private void saveDBTasks(List<TaskEntry<Task>> dbTasks,
			Map<String, ScheduleQueue> queueNumberMap) {
		if (dbTasks != null && dbTasks.size() > 0) {
			for (TaskEntry<Task> taskEntity : dbTasks) {
				try {
					Task realTask = taskEntity.getTask();
					Integer addSuccess = taskService.add(realTask);
					if (addSuccess > 0) {
						removeRedis(taskEntity, queueNumberMap);
					}
				} catch (Throwable e) {
					log.error(desc + "执行saveDBTasks异常" + e.getMessage(),
							e);
				}
			}
		}
	}

	public void setLimitNum(int limitNum) {
		this.limitNum = limitNum;
	}

	public void setKeepRedisNum(int keepRedisNum) {
		this.keepRedisNum = keepRedisNum;
	}

}
