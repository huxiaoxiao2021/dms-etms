package com.jd.bluedragon.distribution.worker.delivery.redis;

import org.springframework.beans.factory.annotation.Autowired;
import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class ReverseDeliveryToTmsRedisTask extends RedisSingleScheduler{
	
	@Autowired
    private ReverseDeliveryService reverseService;

	public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		reverseService.findsendMToReverse(task);
		return true;
	}
}
