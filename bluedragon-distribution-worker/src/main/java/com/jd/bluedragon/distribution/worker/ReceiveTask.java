package com.jd.bluedragon.distribution.worker;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

//@Service
public class ReceiveTask extends DBSingleScheduler {
	private Logger log = LoggerFactory.getLogger(ReceiveTask.class);
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
			log.error("处理收货任务失败[taskId={}]",task.getId(), e);
			return false;
		}
		return true;
	}
}