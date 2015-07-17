package com.jd.bluedragon.distribution.saf.service;

import java.util.List;

import com.jd.bluedragon.distribution.saf.domain.WaybillResponse;
import com.jd.bluedragon.distribution.saf.domain.WaybillSafResponse;


public interface GetWaybillSafService {

	public WaybillSafResponse<List<WaybillResponse>> getOrdersDetails(String boxCode);
	
	public WaybillSafResponse<List<WaybillResponse>> getPackageCodesBySendCode(String sendCode);
}
