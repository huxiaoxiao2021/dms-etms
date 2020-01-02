package com.jd.bluedragon.distribution.saf;

import com.jd.bluedragon.distribution.api.response.WaybillInfoResponse;


/**
 * 提供全国邮政的对外接口，用于邮政查询订单配送详细信息打印邮政面单使用
 * @param waybillcode
 * @return
 */
public interface EmsOrderJosSafService {

	public WaybillInfoResponse getWaybillInfo(String waybillCode); 
}
