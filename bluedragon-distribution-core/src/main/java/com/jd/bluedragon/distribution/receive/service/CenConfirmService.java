package com.jd.bluedragon.distribution.receive.service;

import java.util.List;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;

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
    public CenConfirm fillPickupCode(CenConfirm cenConfirm);
    public CenConfirm fillOperateType(CenConfirm cenConfirm);
    public  void updateOrInsert(CenConfirm cenConfirm);
    public String getTipsMessage(CenConfirm cenConfirm) ;

    public WaybillStatus createWaybillStatus(CenConfirm cenConfirm,
                                             BaseStaffSiteOrgDto bDto, BaseStaffSiteOrgDto rDto);

    /**
     * 构建CenConfirm的通用方式
     * @param inspection
     * @return
     */
    CenConfirm commonGenCenConfirmFromInspection(Inspection inspection);
}
