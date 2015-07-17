package com.jd.bluedragon.distribution.worker.pop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.popPickup.service.PopPickupService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class PopPickupTask extends DBSingleScheduler{
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private PopPickupService popPickupService;

	@Override
	public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		try {
			this.logger.info("task id&type is " + task.getId()+"&"+task.getType());
			this.popPickupService.doPickup(task);
		} catch (Exception e) {
			this.logger.error("task id is" + task.getId());
			this.logger.error("处理分拣任务发生异常，异常信息为：" + e.getMessage(), e);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
