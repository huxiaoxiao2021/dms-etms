package com.jd.bluedragon.distribution.worker.reverse.redis;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class ReverseSendRedisTask extends RedisSingleScheduler {

	@Autowired
	private ReverseSendService reverseSendService;
	
	public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		return reverseSendService.findSendwaybillMessage(task);
	}
}
