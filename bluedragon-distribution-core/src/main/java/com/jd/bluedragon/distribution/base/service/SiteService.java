package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.etms.basic.domain.MainBranchSchedule;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;
import com.jd.etms.basic.dto.BaseTradeInfoDto;

public interface SiteService {

	BaseStaffSiteOrgDto getSite(Integer siteCode);

	BaseTradeInfoDto getTrader(Integer siteCode);
	/**
	 * 通过运力编码获取基础资料信息
	 * @param 运力编码capacityCode
	 * @return
	 */
	public RouteTypeResponse getCapacityCodeInfo(String capacityCode);
	
	/**
	 * 获取基础资料信息
	 * @param 运力编码
	 * @return
	 */
	public CapacityCodeResponse queryCapacityCodeInfo(CapacityCodeRequest request);
	
	/**
	 * 获取基础资料信息
	 * @param 运力编码
	 * @return
	 */
	public MainBranchSchedule getByCapacityCodeInfo(String capacityCode);
}
