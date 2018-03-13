package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.distribution.framework.SendDBSingleScheduler;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 执行发送发货明细MQ的task
 */
public class SendDetailMQTask extends SendDBSingleScheduler{
    
    @Autowired
    private DeliveryService deliveryService;
    
    public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
    	deliveryService.sendDetailMQ(task);
		return true;
	}
}
