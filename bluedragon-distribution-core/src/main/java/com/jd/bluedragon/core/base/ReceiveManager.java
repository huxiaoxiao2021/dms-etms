package com.jd.bluedragon.core.base;

import com.jd.bluedragon.common.domain.RepeatPrint;
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

    /**
     * 获取新单号以及取件单的创建时间是否超过15天【逆向换单业务】
     * @param oldWaybillCode 旧单号
     * @return
     * @throws Exception
     */
    InvokeResult<RepeatPrint> queryDeliveryIdByOldDeliveryId1(String oldWaybillCode);

}
