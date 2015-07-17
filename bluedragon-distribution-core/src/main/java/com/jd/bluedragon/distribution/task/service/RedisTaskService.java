package com.jd.bluedragon.distribution.task.service;

import com.jd.bluedragon.core.redis.TaskModeAware;

public interface RedisTaskService {

	 public boolean add(TaskModeAware task);
}
