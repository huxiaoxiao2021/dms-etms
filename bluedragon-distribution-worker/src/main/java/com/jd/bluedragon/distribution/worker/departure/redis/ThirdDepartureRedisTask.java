package com.jd.bluedragon.distribution.worker.departure.redis;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;

public class ThirdDepartureRedisTask extends RedisSingleScheduler{
    
    @Autowired
	private DepartureService departureService;
    
    public boolean executeSingleTask(Task task, String ownSign)
			throws Exception{
    	return departureService.sendThirdDepartureInfoToTMS(task,false);
    }
		
}
