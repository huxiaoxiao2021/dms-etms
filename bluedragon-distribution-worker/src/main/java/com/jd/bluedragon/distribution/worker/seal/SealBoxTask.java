package com.jd.bluedragon.distribution.worker.seal;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.seal.service.SealBoxService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class SealBoxTask extends DBSingleScheduler {
	
    @Autowired
    private SealBoxService sealService;

	public boolean executeSingleTask(Task task, String ownSign) throws Exception {
		sealService.doSealBox(task);
		return true;
	}
    
}
