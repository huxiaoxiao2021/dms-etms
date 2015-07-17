package com.jd.bluedragon.distribution.worker.pop.redis;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.popInspection.service.PopInspectionService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class PopReceiveRedisTask extends RedisSingleScheduler {

	@Autowired
	private PopInspectionService popInspectionService;

	@Override
	public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		return this.popInspectionService.execute(task);
	}

}
