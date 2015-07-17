package com.jd.bluedragon.distribution.receive.service;

import java.util.List;

import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;

public interface CenConfirmService {
	public CenConfirm createCenConfirmByReceive(Receive receive);
	public CenConfirm createCenConfirmByInspection(Inspection inspection);
	public void  saveOrUpdateCenConfirm(CenConfirm cenConfirm);
	public List<CenConfirm> queryHandoverInfo(CenConfirm cenConfirm);
	public void syncWaybillStatusTask(CenConfirm cenConfirm);
	public WaybillStatus createBasicWaybillStatus(CenConfirm cenConfirm,
			BaseStaffSiteOrgDto bDto, BaseStaffSiteOrgDto rDto);
	public Boolean checkFormat(WaybillStatus tWaybillStatus, Short popType);
	public Task toTask(WaybillStatus tWaybillStatus,Integer smallType);
}
