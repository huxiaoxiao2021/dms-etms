package com.jd.bluedragon.distribution.receive.service.impl;

import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.task.domain.Task;
@Service("receiveTaskExecutor")
public class ReceiveTaskExecutor extends BaseReceiveTaskExecutor<Receive> {
	
	@Override
	public Receive parse(Task task, String ownSign) {
		return receiveService.taskToRecieve(task);
	}
}
