package com.jd.bluedragon.distribution.inventory.service;

import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.handler.WaybillSyncParameter;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

import java.util.List;

public interface PackageStatusService {
    /**
     * 包裹物流状态信息发MQ
     * @param parameters
     * @param bdTraceDto
     */
    void recordPackageStatus(List<WaybillSyncParameter> parameters,BdTraceDto bdTraceDto);

    /**
     * 根据运单号和始发获取目的分拣
     * @param waybillCode
     * @param createSiteCode
     * @return
     */
    BaseStaffSiteOrgDto getReceiveSite(String waybillCode, Integer createSiteCode);
}
