package com.jd.bluedragon.distribution.worker.redis;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.distribution.task.domain.Task;

@Service
public class ReceiveRedisTask extends RedisSingleScheduler {
	private Logger logger = Logger.getLogger(ReceiveRedisTask.class);
	@Autowired
	private ReceiveService receiveService;

	@Override
	protected boolean executeSingleTask(Task task, String ownSign)
			throws Exception {

		try {
			receiveService.doReceiveing(receiveService.taskToRecieve(task));
		} catch (Exception e) {
			logger.error(
					"处理收货任务失败[taskId=" + task.getId() + "]异常信息为："
							+ e.getMessage(), e);
			return false;
		}
		return true;
	}

}