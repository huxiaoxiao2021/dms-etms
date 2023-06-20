package com.jd.bluedragon.distribution.rest.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.sysConfig.request.FuncUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.request.MenuUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.FuncUsageProcessDto;
import com.jd.bluedragon.common.dto.sysConfig.response.MenuUsageProcessDto;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.base.domain.BaseSetConfig;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.domain.VtsBaseSetConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.LoginService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.base.service.UserService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.device.service.DeviceInfoService;
import com.jd.bluedragon.distribution.electron.domain.ElectronSite;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.sdk.modules.menu.dto.MenuPdaRequest;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.api.common.dto.CommonDto;
import com.jd.etms.api.common.enums.RouteProductEnum;
import com.jd.etms.api.recommendroute.resp.RecommendRouteResp;
import com.jd.etms.framework.utils.cache.monitor.CacheMonitor;
import com.jd.etms.sdk.compute.RouteComputeUtil;
import com.jd.etms.vehicle.manager.domain.Vehicle;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.domain.PsStoreInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.dto.SimpleBaseSite;
import com.jd.ql.basic.proxy.BasicPrimaryWSProxy;
import com.jd.tms.basic.dto.BasicDictDto;
import com.jd.tms.basic.dto.CarrierDto;
import com.jd.tms.basic.dto.SimpleCarrierDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.dto.site.AreaVO;
import com.jdl.basic.api.dto.site.BasicSiteVO;
import com.jdl.basic.api.dto.site.ProvinceAgencyVO;
import com.jdl.basic.api.dto.site.SiteQueryCondition;
import com.jdl.basic.common.utils.Pager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class BaseResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final Integer parentGroup = 70550731;
	private static final Integer nodeLevel = 2;
	private static final Integer typeGroup = 70550731;

	@Autowired
	private BaseSetConfig baseSetConfig;
	
	@Autowired
	private VtsBaseSetConfig vtsbaseSetConfig;

	@Autowired
	private BaseService baseService;

    @Autowired
    VmsManager vmsManager;

    @Autowired
    private BaseMinorManager baseMinorManager;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private CacheMonitor cacheMonitor;

	@Autowired
	private SysConfigService sysConfigService;

	@Autowired
	private UserService userService;

	@Autowired
	private DeviceInfoService deviceInfoService;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

	@Autowired
	@Qualifier("basicPrimaryWSProxy")
	private BasicPrimaryWSProxy basicPrimaryWSProxy;

	@Autowired
	private RouteComputeUtil routeComputeUtil;

	@Autowired
	private CarrierQueryWSManager carrierQueryWSManager;

	@Autowired
	private UccPropertyConfiguration uccPropertyConfiguration;

	@Autowired
	private JyBasicSiteQueryManager jyBasicSiteQueryManager;

	@GET
	@Path("/bases/driver/{driverCode}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getDriver", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResponse getDriver(@PathParam("driverCode") String driverCode) {
		this.log.debug("driverCode is {}" , driverCode);
        BaseStaffSiteOrgDto driver = null;
        Integer tmpdriverCode = null;

		try {
			tmpdriverCode = Integer.valueOf(driverCode);
		} catch (Exception e) {
			log.error("司机编码转换失败 {}无法转换成 Integer",driverCode, e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_PARAM_ERROR,
			        JdResponse.MESSAGE_PARAM_ERROR);
			return response;
		}

		try {
			driver = baseService.queryDriverByDriverCode(tmpdriverCode);
		} catch (Exception e) {
			log.error("获取司机失败：driverCode={}",driverCode, e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}

		if (null == driver) {
			log.warn("没有对应司机：driverCode={}",driverCode);
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_DRIVERS_EMPTY);
			return response;
		}

		BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		response.setDriver(driver.getStaffName());
		response.setDriverId(driver.getStaffNo());
		response.setOrgId(driver.getOrgId());
		response.setOrgName(driver.getOrgName());

		return response;
	}

	@GET
	@Path("/bases/saf/vehicle/")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getSafVehicle", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResponse getSafVehicle(@QueryParam("vehicleCode") String vehicleCode,
			@QueryParam("barCode") String barCode) {
		this.log.debug("Saf vehicleCode is {} and barCode is {}" ,vehicleCode, barCode);

        try {
            Vehicle temp = null;
            if ((barCode == null || barCode.isEmpty()) && (vehicleCode == null || vehicleCode.isEmpty()))
                throw new Exception("输入参数车牌号及车辆编码都为空");
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleDeptId("00000842");
            vehicle.setVehicleNumber(vehicleCode);
            vehicle.setVehicleCode(barCode);
            if (vehicleCode != null && !vehicleCode.isEmpty())
                temp = vmsManager.getVehicleInfoByNumber(vehicle);
            if (temp == null && barCode != null && !barCode.isEmpty())
                temp = vmsManager.getVehicleInfoByCode(vehicle);

            if (temp != null) {
				BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
				response.setCarCode(temp.getVehicleCode());
				response.setLicense(temp.getVehicleNumber() );
				return response;
			} else {
				this.log.warn("没有对应车辆:vehicleCode={}",vehicleCode);
				BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
				        JdResponse.MESSAGE_VEHICLES_EMPTY);
				return response;
			}
		} catch (Exception e) {
			this.log.error("Saf 获取车辆失败:vehicleCode={}",vehicleCode, e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}
	}

	@GET
	@Path("/bases/sortingcenter/{code}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getSortingCenter", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResponse getSortingCenter(@PathParam("code") String code) {
		log.debug("sortingcentercode is {}" , code);
		try {
			BaseStaffSiteOrgDto dto = baseService.queryDmsBaseSiteByCode(code);
			log.debug("获取站点信息成功");
			if (dto == null) {
				log.warn("没有对应站点:code={}" , code);
				BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
				        JdResponse.MESSAGE_SITE_EMPTY);
				return response;
			}

			/** 当类型为分拣中心时返回的200，其他返回10000 */
			/** 分拣中心64，二级分拣中心256 */
			if (Constants.BASE_SITE_DISTRIBUTION_CENTER.equals(dto.getSiteType())
			        || Constants.BASE_SITE_DISTRIBUTION_SUBSIDIARY_CENTER.equals(dto.getSiteType())) {
				log.info("站点类型:{} 为分拣中心类型",dto.getSiteType());
				BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
				/** 获取数据 */
				Integer partnerId = dto.getSiteCode();
				String partnerCode = dto.getDmsSiteCode() != null ? dto.getDmsSiteCode() : "";
				String siteName = dto.getSiteName();
				/** 设置属性 */
				response.setPartnerId(partnerId);
				response.setPartnerCode(partnerCode);
				response.setSiteName(siteName);
				/** 返回结果 */
				return response;
			} else {
				log.info("站点类型:{} 非分拣中心类型",dto.getSiteType());
				BaseResponse response = new BaseResponse(JdResponse.CODE_PARAM_ERROR,
				        JdResponse.MESSAGE_SORTINGCENTER_ERROR);
				/** 获取数据 */
				Integer partnerId = dto.getSiteCode();
				String partnerCode = dto.getDmsSiteCode() != null ? dto.getDmsSiteCode() : "";
				String siteName = dto.getSiteName();
				/** 设置属性 */
				response.setPartnerId(partnerId);
				response.setPartnerCode(partnerCode);
				response.setSiteName(siteName);
				/** 返回结果 */
				return response;
			}
		} catch (Exception e) {
			log.error("获取站点名称失败:code={}" , code, e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}
	}

	@GET
	@Path("/bases/site/{code}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getSite", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResponse getSite(@PathParam("code") String code) {
		this.log.debug("sitecode is {}" , code);

        BaseStaffSiteOrgDto dto=null;
		try {
			dto = baseService.queryDmsBaseSiteByCode(code);
		} catch (Exception e) {
			log.error("获取站点名称失败:code={}" , code, e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}

		if (null == dto) {
			log.warn("没有对应站点:code={}" , code);
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_SITE_EMPTY);
			return response;
		}
		BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		response.setSiteCode(dto.getSiteCode());
		response.setSiteName(dto.getSiteName());
		String dmsSiteCode = dto.getDmsSiteCode() != null ? dto.getDmsSiteCode() : "";
		response.setDmsCode(dmsSiteCode);
		response.setSiteType(dto.getSiteType());
		response.setSubType(dto.getSubType());
        response.setOrgId(dto.getOrgId());
		response.setSiteBusinessType(dto.getSiteBusinessType());
		response.setProvinceAgencyCode(dto.getProvinceAgencyCode());
		response.setProvinceAgencyName(dto.getProvinceAgencyName());
		response.setAreaHubCode(dto.getAreaCode());
		response.setAreaHubName(dto.getAreaName());

		return response;
	}

	@POST
	@Path("/bases/login")
	@JProfiler(jKey = "DMS.WEB.BaseResource.login", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResponse login(LoginRequest request) {
		return userService.dmsClientLogin(request);
	}

	@POST
	@Path("/bases/newLogin")
	@JProfiler(jKey = "DMS.WEB.BaseResource.newLogin", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public LoginUserResponse newLogin(LoginRequest request) {
		if (log.isInfoEnabled()) {
			log.info("login from new rest service.[{}]", JsonHelper.toJson(request));
		}
		LoginUserResponse loginUserResponse = new LoginUserResponse();
		if(request == null){
			loginUserResponse.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
			loginUserResponse.setMessage(InvokeResult.PARAM_ERROR);
			return loginUserResponse;
		}
		return ((LoginService)userService).clientLoginIn(request);
	}

	/**
	 * 设备信息上传
	 */
	@POST
	@Path("/bases/deviceInfoUpload")
	@JProfiler(jKey = "DMS.WEB.BaseResource.deviceInfoUpload", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public Object deviceInfoUpload(DeviceInfoRequest request) {
		if (log.isInfoEnabled()) {
			log.info("设备信息上传接口:[{}]", JsonHelper.toJson(request));
		}
		return deviceInfoService.deviceInfoUpload(request);
	}

	@POST
	@Path("/bases/getLoginUser")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getLoginUser", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<LoginUserResponse> getLoginUser(LoginRequest request) {
		return userService.getLoginUser(request);
	}
	@GET
	@Path("/bases/drivers/{orgId}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getDrivers", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getDrivers(@PathParam("orgId") Integer orgId) {
		// 根据机构ID获取对应的司机信息列表
		this.log.debug("orgId is {}" , orgId);
		this.log.debug("获取司机信息开始");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();

		BaseStaffSiteOrgDto[] result = null;
		try {
			result = baseService.queryDriverByOrgId(orgId);
		} catch (Exception e) {
			log.error("获取司机信息失败:orgId={}",orgId, e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result) {
			log.warn("获取司机信息失败:orgId={}",orgId);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (result.length == 0) {
			log.warn("获取司机信息为空:orgId={}",orgId);
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_DRIVERS_EMPTY);
			ll.add(response);
			return ll;
		}

		for (BaseStaffSiteOrgDto dto : result) {
			BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			// 司机姓名
			response.setDriver(dto.getStaffName());
			// 司机ID
			response.setDriverId(dto.getStaffNo());
			ll.add(response);
		}

		return ll;
	}

	@GET
	@Path("/bases/driversByProvince/{provinceAgencyCode}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getDriversOfProvince", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getDriversOfProvince(@PathParam("provinceAgencyCode") String provinceAgencyCode) {
		// 获取省区下的司机信息列表
		List<BaseResponse> driverList = new ArrayList<BaseResponse>();
		// todo 根据省区编码获取省下所有司机的接口

		return driverList;
	}

	@GET
	@Path("/bases/allorgs/")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getAllOrgs", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getAllOrgs() {
		this.log.debug("获取全部机构信息开始");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();

		List<BaseOrg> result = null;

		try {
			result = baseService.getAllOrg();
		} catch (Exception e) {
			log.error("获取全部机构信信息失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result || result.size() == 0) {
			log.warn("获取全部机构信息为空");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_ALLORGS_EMPTY);
			ll.add(response);
			return ll;
		} else {
			BaseResponse bizBaseResponse = new BaseResponse(JdResponse.CODE_OK,
			        JdResponse.MESSAGE_OK);
			bizBaseResponse.setOrgId(-100);
			bizBaseResponse.setOrgName("B商家");
			ll.add(bizBaseResponse);
		}

		for (BaseOrg dto : result) {
			BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			if (null == dto.getOrgId()) {
				response.setOrgId(-1);
			} else {
				response.setOrgId(dto.getOrgId());
			}

			if (null == dto.getOrgName()) {
				response.setOrgName("");
			} else {
				response.setOrgName(dto.getOrgName());
			}
			ll.add(response);
		}

		return ll;
	}


	/**
	 * 查询所有省区
	 *
	 * @return
	 */
	@GET
	@Path("/bases/allProvinceAgency")
	public List<BaseResponse> queryAllProvince() {
		List<BaseResponse> list = Lists.newArrayList();
		List<ProvinceAgencyVO> provinceList = jyBasicSiteQueryManager.queryAllProvinceAgencyInfo();
		if(CollectionUtils.isNotEmpty(provinceList)){
			for (ProvinceAgencyVO item : provinceList) {
				BaseResponse baseResponse = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
				baseResponse.setProvinceAgencyCode(item.getProvinceAgencyCode());
				baseResponse.setProvinceAgencyName(item.getProvinceAgencyName());
				list.add(baseResponse);
			}
		}
		return list;
	}

	/**
	 * 查询省区下所有枢纽
	 *
	 * @return
	 */
	@GET
	@Path("/bases/allArea/{provinceAgencyCode}")
	public List<BaseResponse> queryAllArea(@PathParam("provinceAgencyCode") String provinceAgencyCode) {
		List<BaseResponse> list = Lists.newArrayList();
		List<AreaVO> areaList = jyBasicSiteQueryManager.queryAllAreaInfo(provinceAgencyCode);
		if(CollectionUtils.isNotEmpty(areaList)){
			for (AreaVO item : areaList) {
				BaseResponse baseResponse = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
				baseResponse.setAreaHubCode(item.getAreaCode());
				baseResponse.setAreaHubName(item.getAreaName());
				list.add(baseResponse);
			}
		}
		return list;
	}

	/**
	 * 条件分页查询站点数据
	 *
	 * @return
	 */
	@GET
	@Path("/bases/selectPageSiteList")
	public List<BaseResponse> selectPageSiteList(SiteQueryRequest request) {
		List<BaseResponse> list = Lists.newArrayList();
		Pager<SiteQueryCondition> siteQueryPager = new Pager<>();
//		siteQueryPager.setPageNo();
//		siteQueryPager.setPageSize();
//		siteQueryPager.setSearchVo();
		
		Pager<BasicSiteVO> pagerResult = jyBasicSiteQueryManager.querySitePageByConditionFromBasicSite(siteQueryPager);
		if(pagerResult != null && CollectionUtils.isNotEmpty(pagerResult.getData())){
			for (BasicSiteVO item : pagerResult.getData()) {
				BaseResponse baseResponse = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
				baseResponse.setProvinceAgencyCode(item.getProvinceAgencyCode());
				baseResponse.setProvinceAgencyName(item.getProvinceAgencyName());
				baseResponse.setAreaHubCode(item.getAreaCode());
				baseResponse.setAreaHubName(item.getAreaName());
				baseResponse.setSiteCode(item.getSiteCode());
				baseResponse.setSiteName(item.getSiteName());
				baseResponse.setSiteType(item.getSiteType());
				baseResponse.setSubType(item.getSubType());
				list.add(baseResponse);
			}
		}
		return list;
	}
	
	@GET
	@Path("/bases/sites/{orgId}")
	@GZIP
	@JProfiler(jKey = "DMS.WEB.BaseResource.getSites", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getSites(@PathParam("orgId") Integer orgCode) {
		// 根据机构ID获取对应的站点信息列表
		this.log.debug("orgCode is {}" , orgCode);
		this.log.debug("获取站点信息开始");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();

		BaseStaffSiteOrgDto[] result = null;
		try {
			result = baseService.querySiteByOrgID(orgCode);
		} catch (Exception e) {
			log.error("获取站点信息失败:orgCode={}",orgCode, e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result || result.length == 0) {
			log.warn("获取站点信息为空:orgCode={}",orgCode);
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_SITES_EMPTY);
			ll.add(response);
			return ll;
		}

		for (BaseStaffSiteOrgDto dto : result) {
			BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			// 机构ID
			response.setOrgId(dto.getOrgId());

			// 机构名称
			if (null == dto.getOrgName()) {
				response.setOrgName("");
			} else {
				response.setOrgName(dto.getOrgName());
			}

			// 站点ID
			response.setSiteId(dto.getSiteCode());

			// 站点名称
			if (null == dto.getSiteName()) {
				response.setSiteName("");
			} else {
				response.setSiteName(dto.getSiteName());
			}

			// 站点类型
			response.setSiteType(dto.getSiteType());

			// 对应dmscode的code,旧系统中的数据(int)
			if (null == dto.getSiteCode()) {
				response.setSiteCode(0);
			} else {
				response.setSiteCode(dto.getSiteCode());
			}

			// DMSCODE
			// 等基础服务增加
			if (null == dto.getDmsSiteCode()) {
				response.setDmsCode("");
			} else {
				response.setDmsCode(dto.getDmsSiteCode());
			}
			ll.add(response);
		}

		return ll;
	}

	@GET
	@Path("/bases/errorlist/")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getErrorList", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getErrorList() {

		this.log.debug("获取所有错误信息列表");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();
		BaseDataDict[] result = null;
		try {
			result = baseService.getBaseDataDictListByDate(baseSetConfig.getErroral());
		} catch (Exception e) {
			log.error("获取错误信息列表失败");
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result || result.length == 0) {
			log.warn("获取错误信息为空");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_ERROR_EMPTY);
			ll.add(response);
			return ll;
		}

		for (BaseDataDict dto : result) {
			BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			response.setTypeName(dto.getTypeName());
			response.setTypeCode(dto.getTypeCode());
			response.setTypeGroup(dto.getTypeGroup());
			response.setMemo(dto.getMemo());
			response.setParentId(dto.getParentId());
			response.setNodeLevel(dto.getNodeLevel());
			response.setDataUpdate(dto.getUpdateTime());
			ll.add(response);
		}

		return ll;
	}

    @GET
    @Path("/bases/getBaseDictionaryTreeMulti")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getBaseDictionaryTreeMulti", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<BaseDataDict>> getBaseDictionaryTreeMulti(@QueryParam("typeGroups")String typeGroups){
        InvokeResult<List<BaseDataDict>> result=new InvokeResult<List<BaseDataDict>>();
        result.success();
        try{
			List<BaseDataDict> data= Lists.newArrayList();
			if (typeGroups.indexOf(Constants.SEPARATOR_HYPHEN)!=-1){
				String[] typeGroupArr=typeGroups.split(Constants.SEPARATOR_HYPHEN);
				for(String typeGroup: typeGroupArr){
					data.addAll(baseService.getBaseDictionaryTree(Integer.parseInt(typeGroup)));
				}
				result.setData(data);
			}else{
				result.setData(baseService.getBaseDictionaryTree(Integer.parseInt(typeGroups)));
			}
        }catch (Exception ex){
            log.error("getBaseDictionaryTreeMulti.typeGroups={}",typeGroups,ex);
            result.error(ex);
        }
        return result;
    }
	@GET
	@Path("/bases/getBaseDictionaryTree/{typeGroup}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getBaseDictionaryTree", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InvokeResult<List<BaseDataDict>> getBaseDictionaryTree(@PathParam("typeGroup") int typeGroup){
		InvokeResult<List<BaseDataDict>> result=new InvokeResult<List<BaseDataDict>>();
		result.success();
		try{
			result.setData(baseService.getBaseDictionaryTree(typeGroup));
		}catch (Exception ex){
			log.error("getBaseDictionaryTree:typeGroup={}",typeGroup,ex);
			result.error(ex);
		}
		return result;
	}
	@GET
	@Path("/bases/error/{typeGroup}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getErrorList", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getErrorList(@PathParam("typeGroup") Integer typeGroup) {

		this.log.debug("typeGroup is {}" , typeGroup);
		this.log.debug("获取错误信息列表");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();
		BaseDataDict[] result = null;
		try {
			result = baseService.getBaseDataDictListByDate(typeGroup);
		} catch (Exception e) {
			log.error("获取错误信息列表失败:typeGroup={}",typeGroup);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result || result.length == 0) {
			log.warn("获取错误信息为空:typeGroup={}",typeGroup);
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_ERROR_EMPTY);
			ll.add(response);
			return ll;
		}

		for (BaseDataDict dto : result) {
			BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			response.setTypeName(dto.getTypeName());
			response.setTypeCode(dto.getTypeCode());
			response.setTypeGroup(dto.getTypeGroup());
			response.setMemo(dto.getMemo());
			response.setParentId(dto.getParentId());
			response.setNodeLevel(dto.getNodeLevel());
			response.setDataUpdate(dto.getUpdateTime());
			ll.add(response);
		}

		return ll;
	}

	@GET
	@Path("/bases/sitetype/")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getSiteTypeList", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getSiteTypeList() {
		this.log.debug("获取站点类型信息列表");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();
		BaseDataDict[] result = null;
		try {
			result = baseService.getBaseDataDictListByDate(baseSetConfig.getSitetype());
		} catch (Exception e) {
			log.error("获取站点类型列表失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result || result.length == 0) {
			log.warn("获取站点类型信息为空");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_SITETYPE_EMPTY);
			ll.add(response);
			return ll;
		}

		for (BaseDataDict dto : result) {
			BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			response.setTypeName(dto.getTypeName());
			response.setTypeCode(dto.getTypeCode());
			response.setTypeGroup(dto.getTypeGroup());
			response.setMemo(dto.getMemo());
			response.setParentId(dto.getParentId());
			response.setNodeLevel(dto.getNodeLevel());
			response.setDataUpdate(dto.getUpdateTime());
			ll.add(response);
		}

		return ll;
	}

	@GET
	@Path("/bases/serverdate/")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getServerDate", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResponse getServerDate() {
		BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date nowdate = DateHelper.toDate(System.currentTimeMillis());
		response.setServerDate(formatter.format(nowdate).toString());
		return response;
	}

	/**********************************************************************************/
	@GET
	@Path("/newbases/sites/{orgId}")
	@GZIP
	@JProfiler(jKey = "DMS.WEB.BaseResource.getSites_New", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getSites_New(@PathParam("orgId") Integer orgCode) {
		// 根据机构ID获取对应的站点信息列表
		this.log.debug("orgCode is {}" , orgCode);
		this.log.debug("获取站点信息开始");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();

		BaseStaffSiteOrgDto[] result = null;
		try {
			/*
			 * BaseStaffSiteOrgDto param = new BaseStaffSiteOrgDto();
			 * param.setOrgId(orgCode); result =
			 * baseService.selectSiteByPara(param);
			 */
			result = baseService.querySiteByOrgID(orgCode);
		} catch (Exception e) {
			log.error("获取站点信息失败:orgCode={}",orgCode, e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result || result.length == 0) {
			log.warn("获取站点信息为空:orgCode={}",orgCode);
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_SITES_EMPTY);
			ll.add(response);
			return ll;
		}

		dealSites(Arrays.asList(result), ll, true);

		return ll;
	}


	/**
	 * /newbases/allsite/和/newbases/sites/{orgId}两个接口用来将站点的信息进行处理的
	 *
	 * @param result
	 * @param ll
	 */
	private void dealSites(List<BaseStaffSiteOrgDto> result, List<BaseResponse> ll, boolean isNew) {
		for (BaseStaffSiteOrgDto dto : result) {
			BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			// 机构ID
			response.setOrgId(dto.getOrgId());
			// 机构名称
			response.setOrgName(null == dto.getOrgName() ? "" : dto.getOrgName());
			// 站点ID
			response.setSiteId(dto.getSiteCode());
			// 站点名称
			response.setSiteName(null == dto.getSiteName() ? "" : dto.getSiteName());
			// 站点类型
			response.setSiteType(dto.getSiteType());
			// 站点类型2
			response.setSiteType2(dto.getSubType() != null ? dto.getSubType() : dto.getSiteType());
			// 对应dmscode的code,旧系统中的数据(int)
			response.setSiteCode(null == dto.getSiteCode() ? Integer.valueOf(0) : dto.getSiteCode());
			// DMSCODE
			// 等基础服务增加
			response.setDmsCode(null == dto.getDmsSiteCode() ? "" : dto.getDmsSiteCode());
			// 速递中心
			if (isNew) {
				response.setParentSiteCode(dto.getParentSiteCode());
				response.setParentSiteName(dto.getParentSiteName());
			}
            response.setPinyinCode(dto.getSiteNamePym());
			ll.add(response);
		}
	}

	@GET
	@Path("/newbases/site/{code}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getSite_New", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResponse getSite_New(@PathParam("code") String code) {
		this.log.info("sitecode is {}" , code);

		String siteName;
		Integer siteCode;
		String dmsSiteCode;
		Integer siteType;
		Integer newsiteType;
		Integer parentSiteCode;// 速递id
		String parentSiteName;// 速递名称
		try {
			new BaseStaffSiteOrgDto();
			/*
			 * if(NumberHelper.isStringNumber(code)){
			 * this.log.info("code is siteCode" + code);
			 * param.setSiteCode(NumberHelper.getIntegerValue(code)); }else{
			 * this.log.info("code is dmsCode" + code);
			 * param.setDmsSiteCode(code); }
			 *
			 * BaseStaffSiteOrgDto[] result =
			 * baseService.selectSiteByPara(param);
			 * this.log.info("result have" + result.length);
			 * BaseStaffSiteOrgDto dto = result.length>0?result[0]:null;
			 */
			BaseStaffSiteOrgDto dto = baseService.queryDmsBaseSiteByCode(code);
			siteName = dto != null ? dto.getSiteName() : null;
			siteCode = dto != null ? dto.getSiteCode() : null;
			dmsSiteCode = dto != null && dto.getDmsSiteCode() != null ? dto.getDmsSiteCode() : "";
			siteType = dto != null && dto.getSiteType() != null ? dto.getSiteType() : null;
			newsiteType = dto != null && dto.getSubType() != null ? dto.getSubType() : siteType;
			parentSiteCode = dto != null && dto.getParentSiteCode() != null ? dto
			        .getParentSiteCode() : null;
			parentSiteName = dto != null && dto.getParentSiteName() != null ? dto
			        .getParentSiteName() : null;
		} catch (Exception e) {
			log.error("获取站点名称失败:code={}",code, e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}

		if (null == siteName) {
			log.warn("没有对应站点:code={}",code);
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
                    HintService.getHint(HintCodeConstants.SITE_NOT_EXIST));
			return response;
		}
		BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		response.setSiteCode(siteCode);
		response.setSiteName(siteName);
		response.setDmsCode(dmsSiteCode);
		response.setSiteType(siteType);
		response.setSiteType2(newsiteType);
		response.setParentSiteCode(parentSiteCode);
		response.setParentSiteName(parentSiteName);
		return response;
	}

	@GET
	@Path("/bases/sysconfig/")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getConfigByKey", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<SysConfigResponse> getConfigByKey(@QueryParam("key") String key) {
		this.log.debug("key is {}" , key);
		List<SysConfig> configs = baseService.queryConfigByKey(key+"%");

		List<SysConfigResponse> result = new ArrayList<SysConfigResponse>();

		if (configs == null) {
			log.warn("没有对应参数配置:key={}",key);
			return result;
		} else {
			for (SysConfig config : configs) {
				SysConfigResponse response = new SysConfigResponse();
				response.setCode(JdResponse.CODE_OK);
				response.setMessage(JdResponse.MESSAGE_OK);

				response.setConfigName(config.getConfigName());
				response.setConfigContent(config.getConfigContent());
				response.setConfigType(config.getConfigType());
				response.setConfigOrder(config.getConfigOrder());

				result.add(response);
			}

			return result;
		}

	}

	@GET
	@Path("/base/getBaseSite/{code}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getBaseSite", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public void getBaseSite(@PathParam("code") String code) {
		this.log.debug("sitecode is {}" , code);

		BaseStaffSiteOrgDto bDto = null;
		try {
			bDto = this.baseService.queryDmsBaseSiteByCode(code);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (bDto != null) {
			Integer orderType = bDto.getSiteType();
			Integer baseOrgId = bDto.getOrgId();
			String baseStoreId = bDto.getStoreCode();
			this.log.info("站点类型为 {}" , orderType);
			this.log.info("baseOrgId {}" , baseOrgId);
			this.log.info("baseStoreId {}" , baseStoreId);
		} else {
			this.log.info("site is empty");
		}
	}

	@POST
	@Path("/sysconfig")
	@JProfiler(jKey = "DMS.WEB.BaseResource.modifySysConfig", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public SysConfigResponse modifySysConfig(SysConfig sysConfig) {
		SysConfigResponse response = new SysConfigResponse();

		if (sysConfig == null || sysConfig.getConfigName() == null
		        || sysConfig.getOldPassword() == null || sysConfig.getNewPassword() == null) {
			response.setCode(JdResponse.CODE_PARAM_ERROR);
			response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
			return response;
		}

		List<SysConfig> sysConfigs = this.baseService.queryConfigByKey(sysConfig.getConfigName());
		SysConfig aSysConfig = sysConfigs.get(0);
		if (aSysConfig == null || StringHelper.isEmpty(aSysConfig.getConfigContent())) {
			response.setCode(SysConfigResponse.CODE_OLD_PASSWORD_ERROR);
			response.setMessage(SysConfigResponse.MESSAGE_OLD_PASS_WORD_ERROR);
			return response;
		} else if (!aSysConfig.getConfigContent().equalsIgnoreCase(sysConfig.getOldPassword())) {
			response.setCode(SysConfigResponse.CODE_OLD_PASSWORD_ERROR);
			response.setMessage(SysConfigResponse.MESSAGE_OLD_PASS_WORD_ERROR);
			return response;
		} else {
			sysConfig.setConfigContent(sysConfig.getNewPassword());
			this.baseService.updateSysConfig(sysConfig);

			response.setCode(JdResponse.CODE_OK);
			response.setMessage(JdResponse.MESSAGE_OK);
			return response;
		}

	}

	@GET
	@Path("/sysconfig/")
	@JProfiler(jKey = "DMS.WEB.BaseResource.findSysConfig", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public SysConfigResponse findSysConfig(@QueryParam("key") String key) {
		List<SysConfigResponse> sysConfigs = this.getConfigByKey(key);

		if (!sysConfigs.isEmpty()) {
			return sysConfigs.get(0);
		}

		return noDataResponse();
	}

	private SysConfigResponse noDataResponse() {
		SysConfigResponse response = new SysConfigResponse();
		response.setCode(SysConfigResponse.CODE_NO_DATA);
		response.setMessage(SysConfigResponse.MESSAGE_NO_DATA);
		return response;
	}

	@Path("/getRunNumber/")
	@GET
	@JProfiler(jKey = "DMS.WEB.BaseResource.getRunNumber", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse>  getRunNumber(){
		List<BaseDataDict> dataList =	this.baseService.getBaseDataDictList(6055, 2, 6055);
		List<BaseResponse> responseList = new ArrayList<BaseResponse>();
		for(BaseDataDict data:dataList){
			BaseResponse response= new BaseResponse();
			response.setTypeCode(data.getTypeCode());
			response.setTypeName(data.getTypeName());
			responseList.add(response);

		}
		return responseList;
	}

    /**
     *  根据自提柜站点获取自提柜所属站点
     *  @param code 自提柜编码
     *  @return 自提柜所属站点信息
     * */
	@GET
	@Path("/bases/selfD/{code}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getselfD", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResponse getselfD(@PathParam("code") Integer code) {
		this.log.debug("sitecode is {}" , code);

		Integer sitecode = baseService.getSiteSelfDBySiteCode(code);
		BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		response.setSiteCode(sitecode);

		return response;
	}

	/**
	 *  根据三方-合作站点获取三方-合作站点所属自营站点
	 *  @param code 三方-合作站点ID
	 *  @return 三方-合作站点所属自营站点信息
	 * */
	@GET
	@Path("/bases/threePartner/{code}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getThreePartnerD", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResponse getThreePartnerD(@PathParam("code") Integer code) {
		this.log.debug("sitecode is {}" , code);

		Integer sitecode = baseMajorManager.getPartnerSiteBySiteId(code);
		BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		response.setSiteCode(sitecode);

		return response;
	}

	/**
	 *
	 *	通过运单号获取所属预分拣站点以及所属自营站点
	 *
	 * 	包含以下场景
	 *
	 *  根据自提柜、深度合作自提柜、合作自提代收点 获取所属自营站点
	 *  根据三方-合作站点获取三方-合作站点所属自营站点
	 *  @param
	 *  @return 自营站点信息
	 * */
	@GET
	@Path("/bases/perAndSelfSite/{waybillCode}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.perAndSelfSite", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InvokeResult<List<Integer>> perAndSelfSite(@PathParam("waybillCode") String waybillCode) {
		InvokeResult<List<Integer>> result = new InvokeResult<List<Integer>>();
		List<Integer> siteCodes = new ArrayList<Integer>();
		result.setData(siteCodes);
		try{

			BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, false);
			if(baseEntity!=null && baseEntity.getData()!=null && baseEntity.getData().getWaybill()!=null){
				//获取运单信息
				Waybill waybill = baseEntity.getData().getWaybill();
				//获取预分拣站点信息
				Integer perSiteCode = waybill.getOldSiteId();
				siteCodes.add(perSiteCode);
				BaseStaffSiteOrgDto perSite = baseMajorManager.getBaseSiteBySiteId(perSiteCode);
				if(perSite!=null){
					Integer selfSite = baseService.getMappingSite(waybill.getOldSiteId());
					if(selfSite!=null){
						siteCodes.add(selfSite);
					}
				}else{
					result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
					result.setMessage("未获取到预分拣站点");
				}

			}else{
				result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
				result.setMessage("未获取到运单信息");
			}
		}catch (Exception e){
			log.error("bases/perAndSelfSite error:{}",waybillCode,e);
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
		}
		return result;
	}

	@GET
	@Path("/bases/cache/info")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getCacheInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public Object getCacheInfo() {
		try {
			return cacheMonitor.info();
		} catch (Exception e) {
			return new Integer(-999);
		}
	}

	@GET
	@Path("/bases/cache/memory/{key}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getMemoryElement", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public Object getMemoryElement(@PathParam("key") String key) {
		try {
			return cacheMonitor.getMemoryElement(key);
		} catch (Exception e) {
			return "Error";
		}
	}

	@GET
	@Path("/bases/cache/memory/remove/{key}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.removeMemoryElement", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public Object removeMemoryElement(@PathParam("key") String key) {
		try {
			return cacheMonitor.removeMemoryElement(key);
		} catch (Exception e) {
			return "Error";
		}
	}
	
	@GET
	@Path("/bases/capacityTypelist")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getCapacityTypelist", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getCapacityTypelist() {

		//运力编码需求相关类型
		//(7030,2,7030) //线路类型
		//(7031,2,7031) //运力类型
		//(7032,2,7032) //运输方式
		this.log.info("获取运力的线路类型、运力类型、运输方式 ");

		List<BaseResponse> responseList = new ArrayList<BaseResponse>();
		BaseDataDict[] result = null;
		try {
			//批量提交数据字典信息查询
			result = baseService.getBaseDataDictListByDate(baseSetConfig.getCapacityType());
		} catch (Exception e) {
			//如果异常直接返回
			log.error("获取获取运力信息列表失败");
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			responseList.add(response);
			return responseList;
		}

		if (null == result || result.length == 0) {
			log.warn("获取运力信息为空");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_ERROR_EMPTY);
			responseList.add(response);
			return responseList;
		}

		for (BaseDataDict dto : result) {
			BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			//类型名称
			response.setTypeName(dto.getTypeName());
			//类型编号
			response.setTypeCode(dto.getTypeCode());
			//数据字典分组
			response.setTypeGroup(dto.getTypeGroup());
			//上一级编号
			response.setParentId(dto.getParentId());
			//层级
			response.setNodeLevel(dto.getNodeLevel());
			//修改时间
			response.setDataUpdate(dto.getUpdateTime());
			responseList.add(response);
		}

		return responseList;
	}
	
	/**
	 * 通过运输已有的数据字典查询接口中获取对应线路类型、运输方式、承运商类型3个数据字典项的值
	 * 替换之前的通过接口查询青龙基础资料中的数据字典获取线路类型、运输方式、运力类型
	 * add by lhc
	 * 2016.8.31
	 * @return
	 */
	@GET
	@Path("/bases/capacityTypelists")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getCapacityTypelists", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getCapacityTypelists() {
		//运力编码需求相关类型
		//(1011,2,1011) //线路类型：运输类型
		//(1004,2,1004) //运力类型：承运商类型
		//(1001,2,1001) //运输方式 :运输方式
		this.log.debug("获取运力的线路类型、运力类型、运输方式 ");

		List<BaseResponse> responseList = new ArrayList<BaseResponse>();
		List<BasicDictDto> result = null;
		try {
			//批量提交数据字典信息查询
			result = baseService.getDictListByGroupType(vtsbaseSetConfig.getCapacityType());
		} catch (Exception e) {
			//如果异常直接返回
			log.error("获取获取运力信息列表失败");
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			responseList.add(response);
			return responseList;
		}

		if (CollectionUtils.isEmpty(result)) {
			log.warn("获取运力信息为空");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_ERROR_EMPTY);
			responseList.add(response);
			return responseList;
		}

		for (BasicDictDto dto : result) {
			BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			//类型名称
			response.setTypeName(dto.getDictName());
			//类型编号
			response.setTypeCode(Integer.parseInt(dto.getDictCode()));
			//数据字典分组
			response.setTypeGroup(Integer.parseInt(dto.getDictGroup()));
			//上一级编号
			response.setParentId(Integer.parseInt(dto.getParentCode()));
			//层级
			response.setNodeLevel(dto.getDictLevel());
			//修改时间
			response.setDataUpdate(dto.getUpdateTime());
			responseList.add(response);
		}

		return responseList;
	}
	
	/**
	 * 获取所有的承运商
	 * add by lhc
	 * 2016.9.1
	 * @return
	 */
	@GET
	@Path("/bases/getCarrierInfoList")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getCarrierInfoList", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getCarrierInfoList() {
		List<BaseResponse> responseList = new ArrayList<BaseResponse>();
		CarrierDto carrierParamDto = new CarrierDto();
		carrierParamDto.setOwner("1");
		
		List<CarrierDto> carrierInfoList = baseService.getCarrierInfoList(carrierParamDto);
		
		if (carrierInfoList != null && carrierInfoList.size() > 0) {
			for (CarrierDto carrierInfo : carrierInfoList) {
				BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
				response.setCarrierId(carrierInfo.getCarrierId());
				response.setCarrierCode(carrierInfo.getCarrierCode());
				response.setContacter(carrierInfo.getContacter());
				response.setCarrierName(carrierInfo.getCarrierName());
				response.setAddress(carrierInfo.getAddress());
				responseList.add(response);
			}
		}
		
		return responseList;
	}

	@POST
	@Path("/bases/fuzzyQueryCarrierInfo")
	@JProfiler(jKey = "DMS.WEB.BaseResource.fuzzyQueryCarrierInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InvokeResult<List<SimpleCarrierDto>> fuzzyQueryCarrierInfo(CarrierQueryRequest request) {
		InvokeResult<List<SimpleCarrierDto>> result = new InvokeResult<List<SimpleCarrierDto>>();
		if(!checkAndDealParams(request, result)){
			return result;
		}
		CarrierDto condition = new CarrierDto();
		condition.setCarrierId(request.getCarrierId() == null ? null : Long.parseLong(request.getCarrierId()));
		condition.setCarrierCode(request.getCarrierCode());
		condition.setCarrierName(request.getCarrierName());
		result.setData(carrierQueryWSManager.queryCarrierByLikeCondition(condition));
		return result;
	}

	/**
	 * 参数校验及处理
	 *
	 * @param result
	 * @return
	 */
	private boolean checkAndDealParams(CarrierQueryRequest request, InvokeResult<List<SimpleCarrierDto>> result) {
		result.parameterError(InvokeResult.PARAM_ERROR);
		if(request == null){
			return false;
		}
		if(request.getCarrierId() != null && !NumberHelper.isNumber(request.getCarrierId())){
			return false;
		}
		// 字符串截取长度
		int substringLength = 50;
		if(StringUtils.isNotEmpty(request.getCarrierCode()) && request.getCarrierCode().length() > substringLength){
			request.setCarrierCode(request.getCarrierCode().substring(Constants.NUMBER_ZERO, substringLength));
		}
		if(StringUtils.isNotEmpty(request.getCarrierName()) && request.getCarrierName().length() > substringLength){
			request.setCarrierName(request.getCarrierName().substring(Constants.NUMBER_ZERO, substringLength));
		}
		result.success();
		return true;
	}

	/**
	 *
	 * 分拣细分，分拣中心ID与目的地 获取货柜信息
	 * */
	@GET
	@Path("/bases/goodsposition")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getBaseGoodsPositionDmsCodeSiteCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public ElectronSite getBaseGoodsPositionDmsCodeSiteCode(@QueryParam("createCode") Integer createCode,@QueryParam("receiveCode") Integer receiveCode) {
		return  baseService.getBaseGoodsPositionDmsCodeSiteCode(createCode , receiveCode);

	}

    /**
     * 获取所有签约商家ID列表
     * @return
     */
    @GET
    @Path("/bases/getallsigncustomers")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getAllSignCustomers", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<String>> getAllSignCustomers(){
        InvokeResult<List<String>> result=new InvokeResult<List<String>>();
        result.setCode(200);
        result.setMessage("OK");
        try{
            result.setData(baseMinorManager.getSignCustomer());
        }catch (Exception ex){
            result.setCode(500);
            result.setMessage(ex.getMessage());
        }
        return result;
    }

	/**
	 * 从基础资料获取所有原退商家IDS
	 * */
	@GET
	@Path("/bases/orginalback/{parentGroup}/{nodeLevel}/{typeGroup}")
	@GZIP
	@JProfiler(jKey = "DMS.WEB.BaseResource.getOrignalBackBusIds", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public DatadictResponse getOrignalBackBusIds(@PathParam("parentGroup")Integer parentGroup
												,@PathParam("nodeLevel") Integer nodeLevel
												,@PathParam("typeGroup") Integer typeGroup){
		log.debug("获取原退商家ID,parentGroup={} nodeLevel={} typeGroup={}"
				, parentGroup, nodeLevel, typeGroup);
		DatadictResponse datadictResponse = null;
		try{
			List<BaseDataDict> baseDataDicts = baseMajorManager.getValidBaseDataDictList(
												parentGroup,nodeLevel,typeGroup);
			List<BaseDatadict> bdds = new ArrayList<BaseDatadict>();
			for(BaseDataDict bd : baseDataDicts){
				BaseDatadict bdd = new BaseDatadict();
				bdd.setMemo(bd.getMemo());
				bdd.setTypeCode(bd.getTypeCode());
				bdd.setTypeGroup(bd.getTypeGroup());
				bdd.setTypeName(bd.getTypeName());
				bdd.setUpdateTime(bd.getUpdateTime());
				bdds.add(bdd);
			}
			datadictResponse = new DatadictResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
			datadictResponse.setDatadicts(bdds);
			return datadictResponse;
		}catch (Exception ex){
			log.error("获取原退商家ID失败:parentGroup={} nodeLevel={} typeGroup={}"
					, parentGroup, nodeLevel, typeGroup,ex);
			datadictResponse = new DatadictResponse(JdResponse.CODE_INTERNAL_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
			return datadictResponse;
		}catch (Throwable te){
			log.error("获取原退商家ID失败:parentGroup={} nodeLevel={} typeGroup={}"
					, parentGroup, nodeLevel, typeGroup,te);
			datadictResponse = new DatadictResponse(JdResponse.CODE_INTERNAL_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
			return datadictResponse;
		}
	}

	/**
	 * 从基础资料中获取托寄物
	 * */
	@GET
	@Path("/bases/getConsignments")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getConsignments", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InvokeResult<List<String>> getConsignments(){
		InvokeResult result = new InvokeResult();
		List<String> list = new ArrayList<String>();
		DatadictResponse response =getOrignalBackBusIds(parentGroup,nodeLevel,typeGroup);
		if(response != null && JdResponse.CODE_OK.equals(response.getCode())){
			for (BaseDatadict bd :response.getDatadicts()) {
				list.add(bd.getTypeName());
			}
		}
		result.setCode(JdResponse.CODE_OK);
		result.setData(list);
		return result;
	}

	@GET
	@GZIP
	@Path("/bases/store/{storeType}/{cky2}/{storeID}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getWarehouseByCky2", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public WarehouseResponse getWarehouseByCky2(@PathParam("storeType") String storeType
												,@PathParam("cky2") Integer cky2
												, @PathParam("storeID") Integer storeID){
        WarehouseResponse response = new WarehouseResponse();
        PsStoreInfo psStoreInfo = baseMajorManager.getStoreByCky2(storeType, cky2, storeID);
        if (null == psStoreInfo) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            return response;
        }
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        response.setDmsCode(psStoreInfo.getDmsCode());
        response.setDmsSiteId(psStoreInfo.getDmsSiteId());
        response.setDmsStoreId(psStoreInfo.getDmsStoreId());
        response.setDmsStoreName(psStoreInfo.getDmsStoreName());
        response.setDsmStoreType(psStoreInfo.getDsmStoreType());
        response.setOrgId(psStoreInfo.getOrgId());
        response.setOrgName(psStoreInfo.getOrgName());
        response.setSiteNamePym(psStoreInfo.getSiteNamePym());
        response.setStoreId(psStoreInfo.getStoreId());
        return response;
    }

	@GET
	@GZIP
	@Path("/bases/switch/{conName}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getSwitchStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public SysConfigResponse getSwitchStatus(@PathParam("conName") String conName){
		SysConfigResponse response = new SysConfigResponse();
		List<SysConfig> sysConfigs = null;
		try{
			sysConfigs = sysConfigService.getCachedList(conName);
		}catch(Exception ex){
			log.error("获取开关信息失败:conName={}",conName, ex);
			response.setCode(JdResponse.CODE_SERVICE_ERROR);
			response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}

		if(null == sysConfigs || sysConfigs.size() <= 0){
			response.setCode(JdResponse.CODE_OK_NULL);
			response.setMessage(JdResponse.MESSAGE_OK_NULL);
			return response;
		}

		SysConfig sysConfig = sysConfigs.get(0);
		response.setConfigContent(sysConfig.getConfigContent());
		response.setConfigName(sysConfig.getConfigName());
		response.setConfigOrder(sysConfig.getConfigOrder());
		response.setConfigType(sysConfig.getConfigType());
		response.setCode(JdResponse.CODE_OK);
		response.setMessage(JdResponse.MESSAGE_OK);
		return response;
	}
	
	@GET
	@GZIP
	@Path("/bases/dms/{orgId}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getDmsByOrgId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getDmsByOrgId(@PathParam("orgId") Integer orgId){
		this.log.debug("根据orgId获取分拣中心列表,orgId为:{}" , orgId);
		List<BaseResponse> ll = new ArrayList<BaseResponse>();
		List<SimpleBaseSite> dmsList = new ArrayList<SimpleBaseSite>();
		try{
			dmsList = baseMajorManager.getDmsListByOrgId(orgId);
		}catch(Exception e){
			log.error("根据orgId获取分拣中心列表失败:orgId={}",orgId, e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}
		if(null == dmsList || dmsList.size() == 0){
			log.warn("根据orgId获取分拣中心列表为空:orgId={}",orgId);
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_SITES_EMPTY);
			ll.add(response);
			return ll;
		}
		for(SimpleBaseSite sbs : dmsList){
			BaseResponse br = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			br.setSiteCode(sbs.getSiteCode());;
			br.setSiteName(sbs.getSiteName());
			ll.add(br);
		}
		return ll;
	}
	
	@GET
	@GZIP
	@Path("/bases/dms")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getDmsAll", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getDmsAll(){
		this.log.debug("获取所有的分拣中心");
		List<BaseResponse> ll = new ArrayList<BaseResponse>();
		List<BaseStaffSiteOrgDto> baseSiteList = new ArrayList<BaseStaffSiteOrgDto>();
		try{
			baseSiteList = basicPrimaryWSProxy.getBaseSiteByOrgIdSiteType(null,64);
		}catch(Exception e){
			log.error("获取所有的分拣中心失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}
		if(null == baseSiteList || baseSiteList.size() == 0){
			log.warn("获取所有的分拣中心为空");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_SITES_EMPTY);
			ll.add(response);
			return ll;
		}
		for(BaseStaffSiteOrgDto sbs : baseSiteList){
			BaseResponse br = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			br.setSiteCode(sbs.getSiteCode());
			br.setSiteName(sbs.getSiteName());
			ll.add(br);
		}
		return ll;
	}

	/**
	 * 调用基础资料接口获取所有的转运中心（包括TC + （枢纽+集运）：6420）
	 * @param subTypes
	 * @return
     */
	@GET
	@GZIP
	@Path("/bases/getB2BSiteAll/{subTypes}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getB2BSiteAll", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getB2BSiteAll(@PathParam("subTypes") String subTypes){
		this.log.debug("获取全国所有的转运中心，站点类型：{}" , subTypes);
		List<BaseResponse> result = new ArrayList<BaseResponse>();

		String [] subTypeArray = subTypes.split(",");

		List<BaseOrg> allOrgs = null;
		try {
			log.info("获取全国所有的转运中心-加载全国所有机构");
			allOrgs = baseService.getAllOrg();
		}catch (Exception e){
			log.error("获取全国所有的转运中心失败-加载全国所有机构失败",e);

			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
			result.add(response);
			return result;
		}

		List<BaseStaffSiteOrgDto> siteList = new ArrayList<BaseStaffSiteOrgDto>();

		for(String subTypeStr : subTypeArray) {
			Integer subType = 0;
			try {
				subType = Integer.parseInt(subTypeStr);
			}catch (Exception e){
				log.error("获取全国所有的转运中心失败{}无法转换成Integer类型",subTypeStr);

				BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
						JdResponse.MESSAGE_SERVICE_ERROR);
				result.add(response);
				return result;
			}
			log.info("获取站点类型为：{} 的站点", subType);
			try {
				for (BaseOrg baseOrg : allOrgs) {
					siteList.addAll(baseMajorManager.getBaseSiteByOrgIdSubType(baseOrg.getOrgId(), subType));
				}
			}catch (Exception e){
				log.error("获取全国所有的转运中心失败:subType={}",subType, e);

				BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
						JdResponse.MESSAGE_SERVICE_ERROR);
				result.add(response);
				return result;
			}
		}

		if(siteList == null || siteList.size() <1){
			log.warn("获取全国所有的转运中心列表为空:subTypes={}",subTypes);
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
					JdResponse.MESSAGE_SITES_EMPTY);
			result.add(response);
			return result;
		}

		for(BaseStaffSiteOrgDto dto : siteList){
			BaseResponse br = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			br.setSiteCode(dto.getSiteCode());
			br.setSiteName(dto.getSiteName());
			result.add(br);
		}
		return result;
	}


	/**
	 * 获取所有的仓库
	 * @return
     */
	@GET
	@GZIP
	@Path("/bases/getBaseAllStore/")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getBaseAllStore", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseResponse> getBaseAllStore(){
		this.log.debug("调用基础资料接口获取全国所有库房信息");
		List<BaseResponse> result = new ArrayList<BaseResponse>();

		try {
			List<BaseStaffSiteOrgDto> storeList = baseMajorManager.getBaseAllStore();
			if(storeList == null || storeList.size()<1){
				log.warn("调用基础资料接口获取全国所有库房为空");
				BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
						JdResponse.MESSAGE_SITES_EMPTY);
				result.add(response);
				return result;
			}

			for(BaseStaffSiteOrgDto dto : storeList){
				BaseResponse br = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
				br.setSiteCode(dto.getSiteCode());
				br.setSiteName(dto.getSiteName());
				result.add(br);
			}

		}catch (Exception  e){
			log.error("调用基础资料接口获取全国所有库房信息",e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
			result.add(response);
			return result;
		}

		return result;
	}

    @GET
    @GZIP
    @Path("/bases/getStaffByStaffId/{staffId}")
	@JProfiler(jKey = "DMS.WEB.BaseResource.getStaffByStaffId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseStaffResponse getStaffByStaffId(@PathParam("staffId") Integer staffId){
        BaseStaffSiteOrgDto dto = baseService.getCachedStaffByStaffId(staffId);
        if(null == dto)
        {
            return null;
        }else{
            return toStaffResponse(dto);
        }
    }


    private BaseStaffResponse toStaffResponse(BaseStaffSiteOrgDto dto){
        BaseStaffResponse response = new BaseStaffResponse();
        response.setsId(dto.getsId());
        response.setSiteCode(dto.getSiteCode());
        response.setSiteName(dto.getSiteName());
        response.setStaffName(dto.getStaffName());
        response.setStaffNo(dto.getStaffNo());
        return response;
    }


	@GET
	@Path("/bases/getWaybillRouter/{token}/{startNode}/{endNodeCode}/{operateTime}/{waybillSign}")
	@GZIP
	@JProfiler(jKey = "DMS.WEB.BaseResource.getWaybillRouter", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public CommonDto<RecommendRouteResp> getWaybillRouter(@PathParam("token")String token,
														  @PathParam("startNode")String startNode,
														  @PathParam("endNodeCode")String endNodeCode,
														  @PathParam("operateTime")Long operateTime,
														  @PathParam("waybillSign")String waybillSign){
		//调路由的接口获取路由节点
		Date predictSendTime = new Date(operateTime);
		RouteProductEnum routeProduct = null;
		/**
		 * 当waybill_sign第62位等于1时，确定为B网营业厅运单:
		 * 1.waybill_sign第80位等于1时，产品类型为“特惠运”--TB1
		 * 2.waybill_sign第80位等于2时，产品类型为“特准运”--TB2
		 */
		if(BusinessUtil.isSignChar(waybillSign,62,'1')){
			if(BusinessUtil.isSignChar(waybillSign,80,'1')){
				routeProduct = RouteProductEnum.TB1;
			}else if(BusinessUtil.isSignChar(waybillSign,80,'2')){
				routeProduct = RouteProductEnum.TB2;
			}
		}
		CommonDto<RecommendRouteResp> commonDto = routeComputeUtil.queryRecommendRoute(startNode, endNodeCode, predictSendTime, routeProduct);
		return commonDto;
	}


	@POST
	@Path("menu/pda/account")
	@JProfiler(jKey = "DMS.WEB.BaseResource.menuPdaAccount", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InvokeResult<String> menuPdaAccount(MenuPdaRequest request){
		InvokeResult<String> result = new InvokeResult<>();
		if(log.isDebugEnabled()){
			log.debug("pda常用菜单统计，请求参数:{}" , JsonHelper.toJson(request));
		}

		if(StringUtils.isEmpty(request.getSiteCode())|| StringUtils.isEmpty(request.getOperatorErp())){
			result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
			result.setMessage(InvokeResult.PARAM_ERROR);
			return result;
		}

		//根据机构编码及操作人erp查询
		try{
			String resultJsonStr = baseMajorManager.menuConstantAccount(request.getSiteCode(),request.getOperatorErp(),1);
			result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
			result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
			result.setData(resultJsonStr);
		}catch (Exception e){
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
			log.error("常用功能异常:{}",JsonHelper.toJson(request),e);
		}


		return result;
	}

	@POST
	@Path("menu/print/account")
	@JProfiler(jKey = "DMS.WEB.BaseResource.menuPrintAccount", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InvokeResult<String> menuPrintAccount(MenuPdaRequest request){
		InvokeResult<String> result = new InvokeResult<>();
		if(log.isDebugEnabled()){
			log.debug("打印客户端常用菜单统计，请求参数:{}" , JsonHelper.toJson(request));
		}

		if(StringUtils.isEmpty(request.getSiteCode())|| StringUtils.isEmpty(request.getOperatorErp())){
			result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
			result.setMessage(InvokeResult.PARAM_ERROR);
			return result;
		}

		//根据机构编码及操作人erp查询
		try{
			String resultJsonStr = baseMajorManager.menuConstantAccount(request.getSiteCode(),request.getOperatorErp(),3);
			result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
			result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
			result.setData(resultJsonStr);
		}catch (Exception e){
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
			log.error("常用功能异常:{}",JsonHelper.toJson(request),e);
		}


		return result;
	}

	/**
	 * 基础资料字典获取接口
	 */
	@Path("/getBaseDataDictList/{parentId}/{nodeLevel}/{typeGroup}")
	@GET
	@JProfiler(jKey = "DMS.WEB.BaseResource.getBaseDataDictList", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InvokeResult<List<BaseDataDict>>  getBaseDataDictList(@PathParam("parentId")Integer parentId, @PathParam("nodeLevel")Integer nodeLevel,@PathParam("typeGroup")Integer typeGroup){
        InvokeResult<List<BaseDataDict>> result = new InvokeResult<>();
	    if(parentId == null || nodeLevel == null || typeGroup == null){
			result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
			result.setMessage(InvokeResult.PARAM_ERROR);
			return result;
		}
		try{
			result.setData(this.baseService.getBaseDataDictList(parentId, nodeLevel, typeGroup));
		}catch (Exception e){
	    	log.error("获取基础资料字典数据异常{}，{}，{}",parentId,nodeLevel,typeGroup,e);
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
		}
		return result;
	}


	@POST
	@Path("/bases/checkMenuIsOffline")
	@JProfiler(jKey = "DMS.WEB.BaseResource.checkMenuIsOffline", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InvokeResult<Boolean>  checkMenuIsOffline(PdaSystemMenuRequest request){
		InvokeResult<Boolean> result = new InvokeResult<>();
		// 待下线||已下线PDA菜单编码集合
		String offlinePdaMenuCodes = uccPropertyConfiguration.getOfflinePdaMenuCode();
		boolean menuCodeIsOffline = false;
		if(StringUtils.isNotEmpty(offlinePdaMenuCodes)){
			menuCodeIsOffline = Arrays.asList(offlinePdaMenuCodes.split(Constants.SEPARATOR_COMMA)).contains(request.getMenuCode());
			result.setMessage("此功能已下线，有疑问请咨询分拣小秘（xnfjxm）");
		}
		result.setData(menuCodeIsOffline);
		return result;
	}

    @POST
    @Path("/bases/getAndroidMenuUsageConfig")
    @JProfiler(jKey = "DMS.WEB.BaseResource.getAndroidMenuUsageConfig", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<MenuUsageProcessDto> getAndroidMenuUsageConfig(MenuUsageConfigRequestDto menuUsageConfigRequestDto) {
        InvokeResult<MenuUsageProcessDto> result = new InvokeResult<>();
        result.success();
        try {
            final MenuUsageProcessDto menuUsageProcessDto = baseService.getClientMenuUsageConfig(menuUsageConfigRequestDto);
            result.setData(menuUsageProcessDto);
        } catch (Exception e) {
            log.error("BaseResource.getAndroidMenuUsageConfig exception ", e);
            result.error("接口处理异常");
        }
        return result;
    }
    @POST
    @Path("/bases/getFuncUsageConfig")
    @JProfiler(jKey = "DMS.WEB.BaseResource.getFuncUsageConfig", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<FuncUsageProcessDto> getFuncUsageConfig(FuncUsageConfigRequestDto funcUsageConfigRequestDto) {
        InvokeResult<FuncUsageProcessDto> result = new InvokeResult<>();
        result.success();
        try {
            final FuncUsageProcessDto funcUsageProcessDto = baseService.getFuncUsageConfig(funcUsageConfigRequestDto);
            result.setData(funcUsageProcessDto);
        } catch (Exception e) {
            log.error("BaseResource.getFuncUsageConfig exception ", e);
            result.error("接口处理异常");
        }
        return result;
    }
}


