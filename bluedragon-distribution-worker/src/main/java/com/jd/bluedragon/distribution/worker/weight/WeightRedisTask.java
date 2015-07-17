package com.jd.bluedragon.distribution.worker.weight;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.weight.service.WeightService;

public class WeightRedisTask extends RedisSingleScheduler {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private WeightService weightService;

	@Override
	protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
		boolean result = false;
		try {
			this.logger.info("task id is " + task.getId());
			result = this.weightService.doWeightTrack(task);
		} catch (Exception e) {
			this.logger.error("task id is" + task.getId());
			this.logger.error("处理称重回传任务发生异常，异常信息为：" + e.getMessage(), e);
			return Boolean.FALSE;
		}
		return result;
	}

}
