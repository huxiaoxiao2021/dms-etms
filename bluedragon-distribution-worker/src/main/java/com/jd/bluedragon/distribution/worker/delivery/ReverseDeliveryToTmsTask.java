package com.jd.bluedragon.distribution.worker.delivery;

import org.springframework.beans.factory.annotation.Autowired;
import com.jd.bluedragon.distribution.framework.SendDBSingleScheduler;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class ReverseDeliveryToTmsTask extends SendDBSingleScheduler{
    
    @Autowired
    private ReverseDeliveryService reverseService;
    
    public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
    	reverseService.findsendMToReverse(task);
		return true;
	}
    
}
