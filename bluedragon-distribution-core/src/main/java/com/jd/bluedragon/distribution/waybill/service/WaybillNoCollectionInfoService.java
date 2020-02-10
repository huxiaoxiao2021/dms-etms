package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.waybill.domain.InspectionNoCollectionResult;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionCondition;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionException;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionResult;

public interface WaybillNoCollectionInfoService {

   WaybillNoCollectionResult getWaybillNoCollectionInfo(WaybillNoCollectionCondition waybillNoCollectionCondition);

   WaybillNoCollectionResult getWaybillNoCollectionInfoByBoardCode(WaybillNoCollectionCondition waybillNoCollectionCondition) throws WaybillNoCollectionException;

   InspectionNoCollectionResult getInspectionNoCollectionInfo(WaybillNoCollectionCondition waybillNoCollectionCondition);

   InspectionNoCollectionResult getInspectionNoCollectionInfoBySendCode(WaybillNoCollectionCondition waybillNoCollectionCondition, String sendCode);
}
