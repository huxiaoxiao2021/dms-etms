package com.jd.bluedragon.core.base;

import com.jd.etms.receive.api.request.GrossReturnRequest;
import com.jd.etms.receive.api.response.GrossReturnResponse;

/**
 * @author huangliang
 * 外单组接口
 */
public interface ReceiveManager {

	GrossReturnResponse queryDeliveryIdByFcode(String fCode) throws Exception;

	GrossReturnResponse generateGrossReturnWaybill(GrossReturnRequest grossReturnRequest) throws Exception;

}
