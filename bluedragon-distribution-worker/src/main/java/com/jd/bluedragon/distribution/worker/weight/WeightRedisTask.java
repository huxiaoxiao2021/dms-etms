package com.jd.bluedragon.distribution.worker.weight;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.weight.service.WeightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class WeightRedisTask extends RedisSingleScheduler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private WeightService weightService;

	@Override
	protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
		boolean result = false;
		try {
			this.log.info("task id is {}" , task.getId());
			result = this.weightService.doWeightTrack(task);
		} catch (Exception e) {
			this.log.error("处理称重回传任务发生异常,task id is {}" , task.getId(),e);
			return Boolean.FALSE;
		}
		return result;
	}

}
