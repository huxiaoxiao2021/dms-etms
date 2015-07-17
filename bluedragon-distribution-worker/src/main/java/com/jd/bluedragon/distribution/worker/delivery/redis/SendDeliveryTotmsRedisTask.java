package com.jd.bluedragon.distribution.worker.delivery.redis;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class SendDeliveryTotmsRedisTask extends RedisSingleScheduler {

	@Autowired
	private DeliveryService deliveryService;

	public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		deliveryService.findSendwaybillMessage(task);
		return true;
	}
}
