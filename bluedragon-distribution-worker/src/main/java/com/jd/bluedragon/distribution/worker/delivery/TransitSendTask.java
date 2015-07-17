package com.jd.bluedragon.distribution.worker.delivery;

import org.springframework.beans.factory.annotation.Autowired;
import com.jd.bluedragon.distribution.framework.SendDBSingleScheduler;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class TransitSendTask extends SendDBSingleScheduler{
    
    @Autowired
    private DeliveryService deliveryService;
    
    public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
    	deliveryService.findTransitSend(task);
		return true;
	}
}
