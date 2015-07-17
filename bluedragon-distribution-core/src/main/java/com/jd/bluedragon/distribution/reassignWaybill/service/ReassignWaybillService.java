package com.jd.bluedragon.distribution.reassignWaybill.service;

import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;

public interface ReassignWaybillService {
	 Boolean add(ReassignWaybill packTagPrint);
     ReassignWaybill queryByPackageCode(String packageCode);
	ReassignWaybill queryByWaybillCode(String waybillCode);
}
