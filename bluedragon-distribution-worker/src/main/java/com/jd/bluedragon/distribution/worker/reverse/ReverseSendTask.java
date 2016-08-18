package com.jd.bluedragon.distribution.worker.reverse;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.SendDBSingleScheduler;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class ReverseSendTask extends SendDBSingleScheduler {

	@Autowired
	private ReverseSendService reverseSendService;

	public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		return reverseSendService.findSendwaybillMessage(task);
	}
}
