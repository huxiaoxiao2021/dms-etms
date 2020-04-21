package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.api.response.ColdChainOperationResponse;
import com.jd.ccmp.ctm.dto.WaybillRequest;

/**
 * @author lijie
 * @date 2020/4/4 23:24
 */
public interface WayBillThermometerApiManager {

    ColdChainOperationResponse bindWaybillPackage(WaybillRequest waybillRequest);
}
