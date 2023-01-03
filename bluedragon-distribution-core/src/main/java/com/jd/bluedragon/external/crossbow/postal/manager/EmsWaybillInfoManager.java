package com.jd.bluedragon.external.crossbow.postal.manager;

import com.jd.bluedragon.external.crossbow.postal.domain.WaybillInfoRequest;
import com.jd.bluedragon.external.crossbow.postal.domain.WaybillInfoResponse;

/**
 * <p>
 *     社会快递公司至快递协同信息平台-运单信息传输接口
 *
 * @author wuyoude
 **/
public class EmsWaybillInfoManager extends AbstractPostalCrossbowManager<WaybillInfoRequest, WaybillInfoResponse> {

    /**
     * 请求对象转换
     */
    @Override
    protected WaybillInfoRequest getMyRequestBody(Object condition) {
    	WaybillInfoRequest request = (WaybillInfoRequest)condition;
    	request.setBrandCode(brandCode);
    	request.setPickupAttribute(brandCode);
        return request;
    }

}
