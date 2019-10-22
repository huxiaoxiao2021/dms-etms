package com.jd.bluedragon.distribution.worker;

import com.jd.bluedragon.distribution.receive.domain.Receive;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.distribution.task.domain.Task;

//@Service
public class ReceiveTask extends DBSingleScheduler {
	private Logger logger = Logger.getLogger(ReceiveTask.class);
	@Autowired
	private ReceiveService receiveService;

	@Override
	protected boolean executeSingleTask(Task task, String ownSign)
			throws Exception {

		try {
			Receive receive = receiveService.taskToRecieve(task);
			if(receive != null){
				receiveService.doReceiveing(receive);
			}
		} catch (Exception e) {
			logger.error(
					"处理收货任务失败[taskId=" + task.getId() + "]异常信息为："
							+ e.getMessage(), e);
			return false;
		}
		return true;
	}
}