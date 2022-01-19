package com.jd.bluedragon.distribution.inventory.service;

import java.util.List;

import com.jd.bluedragon.distribution.inventory.domain.SiteWithDirection;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.handler.WaybillSyncParameter;

public interface PackageStatusService {
    /**
     * 包裹物流状态信息发MQ
     * @param parameters
     * @param bdTraceDto
     */
    void recordPackageStatus(List<WaybillSyncParameter> parameters,BdTraceDto bdTraceDto);

    /**
     * 根据运单号和始发获取目的流向
     * @param waybillCode
     * @param createSiteCode
     * @return
     */
    SiteWithDirection getReceiveSiteByWaybillCode(String waybillCode, Integer createSiteCode);
    /**
     * 过滤包裹物流状态信息并发DmsHasnoPresiteWaybillMq，全量接单相关mq
     * @param parameters
     * @param bdTraceDto
     */
    void filterAndSendDmsHasnoPresiteWaybillMq(List<WaybillSyncParameter> parameters,BdTraceDto bdTraceDto);
}
