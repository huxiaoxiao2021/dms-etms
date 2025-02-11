package com.jd.bluedragon.distribution.rest.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.JyBasicSiteQueryManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BasicSiteDto;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.api.request.SiteQueryRequest;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.SiteDesensitization;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.dto.site.BasicSiteVO;
import com.jdl.basic.api.dto.site.SiteQueryCondition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SiteResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 站点查询数量最大限制
	 */
	private static final int MAX_QUERY_LIMIT = 100;

	@Autowired
	private SiteService siteService;

    @Autowired
    private BaseMajorManager baseMajorManager;

	@Autowired
	private DmsConfigManager dmsConfigManager;

	@Autowired
	private JyBasicSiteQueryManager jyBasicSiteQueryManager;

	@GET
	@GZIP
	@Path("/site/{siteCode}")
	@JProfiler(jKey = "DMS.WEB.SiteResource.getSite", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseStaffSiteOrgDto getSite(@PathParam("siteCode") Integer siteCode) {
		BaseStaffSiteOrgDto site = this.siteService.getSite(siteCode);
		SiteDesensitization.desensitizeBaseStaffSiteOrgDto(site);
		return site;
	}


	/** 
	 * 通过运力编码获取基础资料信息
	 * 3/19
	 * cyk
	 * 
	 * */
	@GET
	@Path("/bases/capacityCode/{capacityCode}")
	@JProfiler(jKey = "DMS.WEB.SiteResource.getCapacityCodeInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public RouteTypeResponse getCapacityCodeInfo(@PathParam("capacityCode") String capacityCode) {
		this.log.info("capacityCode is :{}", capacityCode);
        RouteTypeResponse response = new RouteTypeResponse();
		try {
			response = siteService.getCapacityCodeInfo(capacityCode);
		} catch (Exception e) {
			response.setCode(JdResponse.CODE_SERVICE_ERROR);
			response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
			log.error("通过运力编码获取基础资料信息异常：", e);
		}
		
		return response;
	}
	/** 
	 * 通过查询条件获取运力编码信息
	 * 14/5/7
	 * cyk
	 * 
	 * */
	@POST
	@Path("/bases/querycapacitylist")
	@JProfiler(jKey = "DMS.WEB.SiteResource.queryCapacityCodeInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public CapacityCodeResponse queryCapacityCodeInfo(CapacityCodeRequest request) {
		
		CapacityCodeResponse response = new CapacityCodeResponse();
		//校验必要参数是否齐全
		if(request == null){
			//参数不全返回
			response.setCode(JdResponse.CODE_PARAM_ERROR);
			response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
			return response;
		}
		//接口实现
		response = siteService.queryCapacityCodeInfo(request);
		
		return response;
	}

    /**
     * 获取VER站点测试
     * @param siteCode
     * @return
     */
    @GET
    @Path("/bases/siteString/{siteCode}")
	@JProfiler(jKey = "DMS.WEB.SiteResource.getSiteString", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseStaffSiteOrgDto getSiteString(@PathParam("siteCode") String siteCode) {
        return this.baseMajorManager.queryDmsBaseSiteByCodeDmsver(siteCode);
    }

    /**
     * 分页获取所有站点
     * @param pageNo 页号
     * @param category  1：站点---2：库房---3：商家
     * @return
     */
    @GET
    @Path("/site/siteWareHourceMerchantByPage/{category}/{pageNo}")
	@JProfiler(jKey = "DMS.WEB.SiteResource.getSiteByPageNo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Pager<List<SiteWareHouseMerchant>>> getSiteByPageNo(@PathParam("category") int category,@PathParam("pageNo") int pageNo){
		CallerInfo info = Profiler.registerInfo("DMS.siteResource.getSiteByPageNo", Constants.UMP_APP_NAME_DMSWEB,false, true);
    	InvokeResult<Pager<List<SiteWareHouseMerchant>>> result=new InvokeResult<Pager<List<SiteWareHouseMerchant>>>();
        try{
            result.setData(this.siteService.getSitesByPage(category,pageNo));
        }catch (Throwable throwable){
			Profiler.functionError(info);
            log.error("分页获取站点数据失败{}-{}",category,pageNo,throwable);
            result.error("获取站点出现异常，请联系IT运维！");
        }finally {
			Profiler.registerInfoEnd(info);
		}
        return result;
    }
    /**
     * 新接口-分页获取所有站点
     * @param pageNo 当前页码
     * @param category  1：站点---2：库房---3：商家
     * @return
     */
    @GET
    @Path("/site/getSitesWithPageNo/{category}/{pageNo}")
	@JProfiler(jKey = "DMS.WEB.SiteResource.getSitesWithPageNo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Pager<List<SiteWareHouseMerchant>>> getSitesWithPageNo(@PathParam("category") int category,@PathParam("pageNo") int pageNo){
        InvokeResult<Pager<List<SiteWareHouseMerchant>>> result=new InvokeResult<Pager<List<SiteWareHouseMerchant>>>();
        //按站点类型添加监控
        CallerInfo callerInfo = ProfilerHelper.registerInfo("DMS.siteResource.getSitesWithPageNo" + category);
        try{
            result.setData(this.siteService.getSitesByPage(category,pageNo));
        }catch (Throwable throwable){
            log.error("分页获取站点数据失败{}-{}",category,pageNo,throwable);
            result.error("获取站点出现异常，请联系IT运维！");
            Profiler.functionError(callerInfo);
        }finally{
        	Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }


	@GET
	@GZIP
	@Path("/site/getSitesBySendCode/{sendCode}")
	@JProfiler(jKey = "DMS.WEB.SiteResource.getSitesInfoBySendCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InvokeResult<CreateAndReceiveSiteInfo> getSitesInfoBySendCode(@PathParam("sendCode") String sendCode){
		InvokeResult<CreateAndReceiveSiteInfo> result = new InvokeResult<CreateAndReceiveSiteInfo>();

		//验证sendCode
		if(StringHelper.isEmpty(sendCode)){
			log.warn("根据批次号获取始发和目的分拣信息失败，参数批次号为空");
			result.error("根据批次号获取始发和目的分拣信息失败，参数批次号为空");
			return result;
		}
		try{
			//解析批次号，获取始发分拣中心id和目的分拣中心id，0是始发，1是目的
			Integer[] siteCodes = BusinessUtil.getSiteCodeBySendCode(sendCode);
			if (siteCodes[0] == -1 || siteCodes[1] == -1) {
				log.warn("根据批次号获取始发和目的分拣信息失败，批次号:{} 始发分拣code:{} ,目的分拣Code:{}",sendCode, siteCodes[0], siteCodes[1]);
				result.error("根据批次号获取始发和目的分拣信息失败，批次号：" + "始发分拣code:" + siteCodes[0] + ",目的分拣Code:" + siteCodes[1]);
				return result;
			}

			//根据站点id获取站点信息，并将始发站点信息和目的站点信息映射到CreateAndReceiveSiteInfo对象中
			CreateAndReceiveSiteInfo createAndReceiveSite = new CreateAndReceiveSiteInfo();
			BaseStaffSiteOrgDto createSite = siteService.getSite(siteCodes[0]);
			BaseStaffSiteOrgDto receiveSite = siteService.getSite(siteCodes[1]);

			//始发站点信息的映射
			if(createSite != null){
				createAndReceiveSite.setCreateSiteCode(createSite.getSiteCode());
				createAndReceiveSite.setCreateSiteName(createSite.getSiteName());
				createAndReceiveSite.setCreateSiteType(createSite.getSiteType());
				createAndReceiveSite.setCreateSiteSubType(createSite.getSubType());
			}

			//目的站点信息的映射
			if(receiveSite != null){
				createAndReceiveSite.setReceiveSiteCode(receiveSite.getSiteCode());
				createAndReceiveSite.setReceiveSiteName(receiveSite.getSiteName());
				createAndReceiveSite.setReceiveSiteType(receiveSite.getSiteType());
				createAndReceiveSite.setReceiveSiteSubType(receiveSite.getSubType());
			}

			result.setMessage("success");
			result.setData(createAndReceiveSite);
		}catch (Exception e){
			log.error("根据批次号获取始发和目的分拣信息失败，批次号：{}", sendCode, e);
			result.error("根据批次号获取始发和目的分拣信息出现异常，请联系org.wlxt2");
		}

		return result;
	}

	/**
	 * 获取站点信息
	 * 根据Code精确匹配和那么模糊匹配
	 * 模糊做多返回20条
	 * @return list
     */
	@GET
	@Path("/bases/getSiteByCodeOrName/{siteCodeOrName}")
	@JProfiler(jKey = "DMS.WEB.SiteResource.getSiteByCodeOrName", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InvokeResult<List<BaseStaffSiteOrgDto>> getSiteByCodeOrName(@PathParam("siteCodeOrName") String siteCodeOrName) {
		InvokeResult<List<BaseStaffSiteOrgDto>> result = new InvokeResult<>();
		if (StringHelper.isEmpty(siteCodeOrName)) {
			return result;
		}
		List<BaseStaffSiteOrgDto> resList = new ArrayList<>();
		if (NumberHelper.isNumber(siteCodeOrName)) {
			/* 站点code精确匹配 */
			resList.add(baseMajorManager.queryDmsBaseSiteByCodeDmsver(siteCodeOrName));
		} else {
			/* 站点名称模糊匹配 */
			resList.addAll(siteService.fuzzyGetSiteBySiteName(siteCodeOrName));
		}
		result.setData(resList);
		return result;
	}

	/**
	 * 根据条件查询站点
	 * @param request
	 * @return
	 */
	@POST
	@Path("/bases/querySiteFromStreamlinedSite")
	public InvokeResult<List<BasicSiteDto>> querySiteFromStreamlinedSite(SiteQueryRequest request) {
		InvokeResult<List<BasicSiteDto>> result = new InvokeResult<>();
		if(request == null || request.getFetchNum() == null){
			result.parameterError(InvokeResult.PARAM_ERROR);
			return result;
		}
		// 查询数量限制：100
		Integer siteQueryLimit = dmsConfigManager.getPropertyConfig().getSiteQueryLimit() == null
				? MAX_QUERY_LIMIT : dmsConfigManager.getPropertyConfig().getSiteQueryLimit();
		if(request.getFetchNum() > siteQueryLimit){
			request.setFetchNum(siteQueryLimit);
		}
		// 查询拣运基础站点获取数据
		com.jdl.basic.common.utils.Pager<SiteQueryCondition> siteQueryPager = new com.jdl.basic.common.utils.Pager<>();
		siteQueryPager.setPageNo(Constants.NUMBER_ONE);
		siteQueryPager.setPageSize(request.getFetchNum());
		siteQueryPager.setSearchVo(convertToPageQueryCondition(request));
		com.jdl.basic.common.utils.Pager<BasicSiteVO> pagerBaseEntity = jyBasicSiteQueryManager.querySitePageByConditionFromBasicSite(siteQueryPager);
		if(pagerBaseEntity != null && CollectionUtils.isNotEmpty(pagerBaseEntity.getData())){
			result.setData(convertToBasicSite(pagerBaseEntity.getData()));
		}
		return result;
	}

	private SiteQueryCondition convertToPageQueryCondition(SiteQueryRequest request) {
		SiteQueryCondition basicSiteQuery = new SiteQueryCondition();
		if(request.getSiteId() != null && request.getSiteId() > Constants.NUMBER_ZERO){
			basicSiteQuery.setSiteCode(request.getSiteId());
		}
		if(StringUtils.isNotEmpty(request.getSiteName())){
			basicSiteQuery.setSiteName(request.getSiteName());
		}
		if(StringUtils.isNotEmpty(request.getDmsCode())){
			basicSiteQuery.setDmsCode(request.getDmsCode());
		}
		if(StringUtils.isNotEmpty(request.getSiteNamePym())){
			basicSiteQuery.setSiteNamePym(request.getSiteNamePym());
		}
		if(CollectionUtils.isNotEmpty(request.getSiteTypeList())){
			basicSiteQuery.setSiteTypes(request.getSiteTypeList());
		}
		if(CollectionUtils.isNotEmpty(request.getSubTypeList())){
			basicSiteQuery.setSubTypes(request.getSubTypeList());
		}
		if(request.getOrgId() != null && request.getOrgId() > Constants.NUMBER_ZERO){
			basicSiteQuery.setOrgId(request.getOrgId());
		}
		if(StringUtils.isNotEmpty(request.getProvinceAgencyCode())){
			basicSiteQuery.setProvinceAgencyCode(request.getProvinceAgencyCode());
		}
		if(request.getProvinceId() != null && request.getProvinceId() > Constants.NUMBER_ZERO){
			basicSiteQuery.setProvinceId(request.getProvinceId());
		}
		if(request.getCityId() != null && request.getCityId() > Constants.NUMBER_ZERO){
			basicSiteQuery.setCityId(request.getCityId());
		}
		if(request.getCountryId() != null && request.getCountryId() > Constants.NUMBER_ZERO){
			basicSiteQuery.setCountryId(request.getCountryId());
		}
		return basicSiteQuery;
	}

	/**
	 * 转换为客户端站点对象
	 * @param basicSiteList
	 * @return
	 */
	private List<BasicSiteDto> convertToBasicSite(List<BasicSiteVO> basicSiteList) {
		List<BasicSiteDto> list = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(basicSiteList)){
			for (BasicSiteVO site : basicSiteList){
				BasicSiteDto basicSiteDto = new BasicSiteDto();
				BeanUtils.copyProperties(site,basicSiteDto);
				basicSiteDto.setId(site.getSiteCode());
				basicSiteDto.setName(site.getSiteName());
				basicSiteDto.setDmsCode(site.getDmsSiteCode());
				basicSiteDto.setType(site.getSiteType());
				basicSiteDto.setPinyinCode(site.getSiteNamePym());
				basicSiteDto.setSortingCenterId(site.getDmsId());
				basicSiteDto.setSortingCenterName(site.getDmsName());
				basicSiteDto.setParentId(site.getParentSiteCode());
				basicSiteDto.setParentName(site.getParentSiteName());
				list.add(basicSiteDto);
			}
		}
		return list;
	}

}
