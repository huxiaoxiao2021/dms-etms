package com.jd.bluedragon.distribution.worker.pop;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.popPickup.service.PopPickupService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PopPickupTask extends DBSingleScheduler{
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PopPickupService popPickupService;

	@Override
	public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		try {
			this.log.info("task id&type is {}&{}" , task.getId(),task.getType());
			this.popPickupService.doPickup(task);
		} catch (Exception e) {
			this.log.error("处理分拣任务发生异常,task id is {}" , task.getId(),e);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
