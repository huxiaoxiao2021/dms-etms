package com.jd.bluedragon.distribution.worker;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.receiveInspectionExc.service.ShieldsErrorService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class ShieldsCarErrorRedisTask extends RedisSingleScheduler {
   
    @Autowired
    private ShieldsErrorService shieldsErrorService;

    public boolean executeSingleTask(Task task, String ownSign) throws Exception {
      return shieldsErrorService.doAddShieldsError(shieldsErrorService.doParseShieldsCar(task));
	}

   
}
