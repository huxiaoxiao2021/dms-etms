package com.jd.bluedragon.distribution.worker;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.partnerWaybill.service.PartnerWaybillService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class PartnerWaybillRedisTask extends RedisSingleScheduler {

	@Autowired
	private PartnerWaybillService partnerWaybillService;

	public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		try {
			partnerWaybillService.doCreateWayBillCode(partnerWaybillService
					.doParse(task));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}