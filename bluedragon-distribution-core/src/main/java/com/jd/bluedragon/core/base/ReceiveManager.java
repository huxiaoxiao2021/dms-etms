package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.etms.receive.api.request.GrossReturnRequest;
import com.jd.etms.receive.api.response.GrossReturnResponse;

/**
 * @author huangliang
 * 外单组接口
 */
public interface ReceiveManager {

	GrossReturnResponse queryDeliveryIdByFcode(String fCode) throws Exception;

	GrossReturnResponse generateGrossReturnWaybill(GrossReturnRequest grossReturnRequest) throws Exception;

    /**
     * 获取新单号【逆向换单业务】
     * @param oldWaybillCode 旧单号
     * @return
     * @throws Exception
     */
    InvokeResult<String> queryDeliveryIdByOldDeliveryId(String oldWaybillCode);

}
