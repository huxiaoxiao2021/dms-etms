package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

import java.util.List;
import java.util.Set;

public interface SiteService {

	BaseStaffSiteOrgDto getSite(Integer siteCode);

	BasicTraderInfoDTO getTrader(Integer siteCode);
	/**
	 * 通过运力编码获取基础资料信息
	 * @param capacityCode 运力编码
	 * @return
	 */
	public RouteTypeResponse getCapacityCodeInfo(String capacityCode);
	
	/**
	 * 获取基础资料信息
	 * @param request 运力编码
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
     * 根据批次号获取 始发与目的信息
     * @param sendCode 批次号
     * @return 始发与目的信息
     */
    CreateAndReceiveSiteInfo getCreateAndReceiveSiteBySendCode (String sendCode);
    /**
     * 获取北京的分拣中心
     * @return
     */
    Set<Integer> getBjDmsSiteCodes();

	/**
	 * 从sysconfig表里查出来开放C网路由校验的分拣中心列表
	 * @return
	 */
	Set<Integer> getCRouterAllowedList();

	Set<Integer> getCRouterAllowedListNoCache();

	/**
	 * 修改sysconfig表里查出来开放C网路由校验的分拣中心列表
	 * @param siteCode
	 * @param openFlag
	 * @return
	 */
	boolean changeRouterConfig(Integer siteCode,boolean openFlag);
	/**
	 * 从sysconfig表里查出来需要通过中台生产箱号的分拣中心列表
	 * @return
	 */
	public Set<Integer> getBoxFromSSCAllowedList();

	/**
	 * 从sysconfig表里查出来需要通过DMS生产箱号的分拣中心列表
	 * @return
	 */
	public Set<Integer> getBoxFromDMSAllowedList();
	/**
	 * 根据省id获取分拣中心
	 * @param provinceId
	 * @return
	 */
	public List<BaseStaffSiteOrgDto> getDmsListByProvince(Integer provinceId );

	/**
	 * 获取所有分拣中心
	 * @return
	 */
	List<BaseStaffSiteOrgDto> getAllDmsSite();

	/**
	 * 根据城市id获取分拣中心
	 * @param cityId
	 * @return
	 */
	public List<BaseStaffSiteOrgDto> getDmsListByCity(Integer cityId);

	/**
	 * 根据orgId获取分拣中心
	 * @param areaId
	 * @return
	 */
	public List<BaseStaffSiteOrgDto> getDmsListByAreaId(Integer areaId);

	/**
	 * 根据站点id获取站点名称
	 * @param siteCode
	 * @return
     */
	String getSiteNameByCode(Integer siteCode);

	/**
	 * 获取城市绑定的分拣中心编码
	 * @param cityId
	 * @return
	 */
	Integer getCityBindDmsCode(Integer cityId);
	/**
	 * 从系统配置表sysconfig，根据配置名称获取站点编码列表，站点编码以‘,’隔开
	 * @param sysConfigName
	 * @return
	 */
	Set<Integer> getSiteCodesFromSysConfig(String sysConfigName);


	/**
	 * 根据站点名称进行模糊匹配查询
	 * @param siteName 站点名称
	 * @return
	 */
	List<BaseStaffSiteOrgDto> fuzzyGetSiteBySiteName(String siteName);


	Site get(Integer siteCode);

	Site getNoCache(Integer siteCode);
	/*
	* 获取返调度站点
	* */
	Integer getLastScheduleSite(String packageCode);

	/**
	 * 根据分拣中心编码获取分拣中心名称并截取掉 ”分拣中心","中转场","分拨中心"等
	 * @param dmsCode
	 * @return
	 */
	String getDmsShortNameByCode(Integer dmsCode);

	/**
	 * 获取站点
	 * 
	 * @param siteCode
	 * @return
	 */
	Site getOwnSite(Integer siteCode);
}
