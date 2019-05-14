package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionCondition;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionException;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionResult;

import java.util.Map;

public interface WaybillNoCollectionInfoService {

   WaybillNoCollectionResult getWaybillNoCollectionInfo(WaybillNoCollectionCondition waybillNoCollectionCondition);

   WaybillNoCollectionResult getWaybillNoCollectionInfoByBoardCode(WaybillNoCollectionCondition waybillNoCollectionCondition) throws WaybillNoCollectionException;

}
