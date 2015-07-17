package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.reverse.domain.ReverseSendAsiaWms;
import com.jd.bluedragon.distribution.reverse.domain.WmsSite;

public interface BaseWmsService {
	/**
	 * 根据订单号包裹号查询订单信息
	 * @param orderCode
	 * @return ReverseSendWms 由于新建对象, 所以返回一定非空值
	 */
	ReverseSendAsiaWms getWaybillByOrderCode(String orderCode,String packcodes, WmsSite site,boolean falge);
	
}
