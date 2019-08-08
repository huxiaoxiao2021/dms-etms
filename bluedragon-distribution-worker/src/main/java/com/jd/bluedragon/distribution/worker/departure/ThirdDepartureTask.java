package com.jd.bluedragon.distribution.worker.departure;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.framework.SendDBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;

public class ThirdDepartureTask extends SendDBSingleScheduler{
    
    @Autowired
	private DepartureService departureService;
    
    public boolean executeSingleTask(Task task, String ownSign)
			throws Exception{
    	return departureService.sendThirdDepartureInfoToTMS(task,true);
    }
		
}
