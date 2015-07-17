package com.jd.bluedragon.distribution.worker;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.partnerWaybill.service.PartnerWaybillService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class PartnerWaybillTask extends DBSingleScheduler {

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