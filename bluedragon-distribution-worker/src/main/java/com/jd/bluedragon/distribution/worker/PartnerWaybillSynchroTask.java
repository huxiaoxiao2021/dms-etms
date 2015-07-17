package com.jd.bluedragon.distribution.worker;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.partnerWaybill.service.PartnerWaybillService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class PartnerWaybillSynchroTask extends AbstractScheduleTask {
	private Logger logger = Logger.getLogger(PartnerWaybillSynchroTask.class);

	@Autowired
	private PartnerWaybillService partnerWaybillService;

	public boolean execute(Object[] taskArray, String arg1) throws Exception {
		if (taskArray == null || taskArray.length == 0) {
			return false;
		}
		try {
			List<Task> taskList = this.asList(taskArray);
			partnerWaybillService.doWayBillCodesProcessed(taskList);
		} catch (Exception e) {
			logger.error("处理运单号关联包裹数据时发生异常", e);
		}
		return true;
	}

}