package com.jd.bluedragon.distribution.reassignWaybill.service;

import com.jd.bluedragon.distribution.api.request.ReassignWaybillRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;

public interface ReassignWaybillService {
     ReassignWaybill queryByPackageCode(String packageCode);
	ReassignWaybill queryByWaybillCode(String waybillCode);
	/**
	 * 现场预分拣回调处理
	 */
	JdResult<Boolean> backScheduleAfter(ReassignWaybillRequest reassignWaybillRequest);
}
