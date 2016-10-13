package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.dto.BaseTradeInfoDto;

import java.util.List;

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
     * 获取站点分页
     * @param pageNo
     * @return
     */
    public Pager<List<SiteWareHouseMerchant>> getSitesByPage(int category,int pageNo);


}
