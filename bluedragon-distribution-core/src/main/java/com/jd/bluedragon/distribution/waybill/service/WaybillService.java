package com.jd.bluedragon.distribution.waybill.service;

import com.jd.etms.waybill.dto.BigWaybillDto;

public interface WaybillService {

	BigWaybillDto getWaybill(String waybillCode);

	BigWaybillDto getWaybillProduct(String waybillCode);
	
	/**
	 * 获取运单状态接口
	 * */
    BigWaybillDto getWaybillState(String waybillCode);

}
