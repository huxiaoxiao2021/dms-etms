package com.jd.bluedragon.distribution.base.service;

import java.util.List;
import java.util.Set;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

public interface SiteService {

	BaseStaffSiteOrgDto getSite(Integer siteCode);

	BasicTraderInfoDTO getTrader(Integer siteCode);
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

	/**
	 * 根据批次号获取始发分拣中心id和目的分拣中心id
	 * @param sendCode
     */
	public Integer[]  getSiteCodeBySendCode (String sendCode);
    /**
     * 获取北京的分拣中心
     * @return
     */
    Set<Integer> getBjDmsSiteCodes();
}
