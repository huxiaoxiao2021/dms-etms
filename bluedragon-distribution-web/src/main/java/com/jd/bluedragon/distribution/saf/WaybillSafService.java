package com.jd.bluedragon.distribution.saf;



public interface WaybillSafService{
	/**
	 * PDA POST请求查询锁定订单信息 参数PdaOperateRequest(属性:getPackageCode)
		返回值 WaybillResponse(属性Integer:code String:message)
		
		/waybills/cancel?packageCode=xx PDA GET请求查询锁定订单信息 参数包裹号（String）
		返回值 WaybillResponse(属性Integer:code String:message) 
		锁定信息内容：取消订单，删除订单，锁定订单，退款100分，拦截订单
		返回值 CODE范围 取消订单(29302)，删除订单(29302) ，锁定订单(29301) ，退款100分(29303) ，拦截订单(29305) 
	 * @param packageCode
	 * @return
	 */
	public WaybillSafResponse isCancel(String packageCode);
	
}
