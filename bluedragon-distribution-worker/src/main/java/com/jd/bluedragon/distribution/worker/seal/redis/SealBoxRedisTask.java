package com.jd.bluedragon.distribution.worker.seal.redis;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.seal.service.SealBoxService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class SealBoxRedisTask extends RedisSingleScheduler {

	@Autowired
	private SealBoxService sealService;

	@Override
	public boolean executeSingleTask(Task task, String ownSign) throws Exception {
		this.sealService.doSealBox(task);
		return true;
	}

}
