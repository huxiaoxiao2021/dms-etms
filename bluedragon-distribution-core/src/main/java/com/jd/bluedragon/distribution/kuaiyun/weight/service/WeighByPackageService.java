package com.jd.bluedragon.distribution.kuaiyun.weight.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.impl.ErpUserClient;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

import java.util.List;
import java.util.Map;

public interface WeighByPackageService {
    InvokeResult<Boolean> verifyPackageReality(String codeStr);

    boolean validateParam(WaybillWeightVO waybillWeightVO);

    InvokeResult checkIsExcess(String codeStr, String toString, String toString1);

    InvokeResult<Boolean> insertWaybillWeight(WaybillWeightVO waybillWeightVO, ErpUserClient.ErpUser erpUser, BaseStaffSiteOrgDto baseStaffSiteOrgDto);

    boolean checkOfImportPackage(WaybillWeightVO waybillWeightVO, ErpUserClient.ErpUser erpUser, BaseStaffSiteOrgDto bssod);

    List<List<Object>> getExportDataPackage(List<Map> list);
}
