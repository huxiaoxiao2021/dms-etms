package com.jd.bluedragon.distribution.rest.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.VmsManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BaseRequest;
import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.BaseSetConfig;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.PdaStaff;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.domain.VtsBaseSetConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.client.JsonUtil;
import com.jd.bluedragon.distribution.electron.domain.ElectronSite;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.bluedragon.distribution.sysloginlog.domain.SysLoginLog;
import com.jd.bluedragon.distribution.sysloginlog.service.SysLoginLogService;
import com.jd.bluedragon.utils.BaseContants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.framework.utils.cache.monitor.CacheMonitor;
import com.jd.etms.vehicle.manager.domain.Vehicle;
import com.jd.etms.vts.dto.CarrierInfo;
import com.jd.etms.vts.dto.CarrierParamDto;
import com.jd.etms.vts.dto.CommonDto;
import com.jd.etms.vts.dto.DictDto;
import com.jd.etms.vts.ws.VtsQueryWS;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.domain.PsStoreInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.dto.SimpleBaseSite;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
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

	private final Log logger = LogFactory.getLog(this.getClass());
	private final String DMS = "dms";

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
	private SysLoginLogService sysLoginLogService;

	@Autowired
	private VtsQueryWS vtsQueryWS;

	@GET
	//Path("/bases/allsite/")
	@GZIP
	@Deprecated
	public List<BaseResponse> getDmsSiteAll() {
		this.logger.info("查询所有站点信息");
		List<BaseResponse> ll = new ArrayList<BaseResponse>();
		List<BaseStaffSiteOrgDto> results = null;
		try {
			results = baseService.getDmsSiteAll();
		} catch (Exception e) {
			logger.error("查询所有站点信息失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (results == null || results.size() == 0) {
			logger.info("查询所有站点信息，没有查询到");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_ALLSITE_EMPTY);
			ll.add(response);
			return ll;
		}

		for (BaseStaffSiteOrgDto dto : results) {
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
	@Path("/bases/driver/{driverCode}")
	public BaseResponse getDriver(@PathParam("driverCode") String driverCode) {
		this.logger.info("driverCode is " + driverCode);
        BaseStaffSiteOrgDto driver = null;
        Integer tmpdriverCode = null;

		try {
			tmpdriverCode = Integer.valueOf(driverCode);
		} catch (Exception e) {
			logger.error("司机编码转换失败 " + driverCode + "无法转换成 Integer", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_PARAM_ERROR,
			        JdResponse.MESSAGE_PARAM_ERROR);
			return response;
		}

		try {
			driver = baseService.queryDriverByDriverCode(tmpdriverCode);
		} catch (Exception e) {
			logger.error("获取司机失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}

		if (null == driver) {
			logger.error("没有对应司机");
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
	public BaseResponse getSafVehicle(@QueryParam("vehicleCode") String vehicleCode,
			@QueryParam("barCode") String barCode) {
		this.logger.info("Saf vehicleCode is " + vehicleCode + " and barCode is " + barCode);

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
				this.logger.error("没有对应车辆");
				BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
				        JdResponse.MESSAGE_VEHICLES_EMPTY);
				return response;
			}
		} catch (Exception e) {
			this.logger.error("Saf 获取车辆失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}
	}

	@GET
	@Path("/bases/sortingcenter/{code}")
	public BaseResponse getSortingCenter(@PathParam("code") String code) {
		logger.info("sortingcentercode is " + code);
		try {
			BaseStaffSiteOrgDto dto = baseService.queryDmsBaseSiteByCode(code);
			logger.info("获取站点信息成功");
			if (dto == null) {
				logger.error("没有对应站点");
				BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
				        JdResponse.MESSAGE_SITE_EMPTY);
				return response;
			}

			/** 当类型为分拣中心时返回的200，其他返回10000 */
			/** 分拣中心64，二级分拣中心256 */
			if (Constants.BASE_SITE_DISTRIBUTION_CENTER.equals(dto.getSiteType())
			        || Constants.BASE_SITE_DISTRIBUTION_SUBSIDIARY_CENTER.equals(dto.getSiteType())) {
				logger.info("站点类型:" + dto.getSiteType() + " 为分拣中心类型");
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
				logger.info("站点类型:" + dto.getSiteType() + " 非分拣中心类型");
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
			logger.error("获取站点名称失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}
	}

	@GET
	@Path("/bases/site/{code}")
	public BaseResponse getSite(@PathParam("code") String code) {
		this.logger.info("sitecode is " + code);

		String siteName;
		Integer siteCode;
		String dmsSiteCode;
		Integer siteType;
		Integer siteBusType;
        Integer orgId;
        BaseStaffSiteOrgDto dto=null;
		try {
			dto = baseService.queryDmsBaseSiteByCode(code);
			siteName = dto != null ? dto.getSiteName() : null;
			siteCode = dto != null ? dto.getSiteCode() : null;
			dmsSiteCode = dto != null && dto.getDmsSiteCode() != null ? dto.getDmsSiteCode() : "";
			siteType = dto != null && dto.getSiteType() != null ? dto.getSiteType() : null;
			orgId = dto != null && dto.getOrgId() != null ? dto.getOrgId() : null;
            siteBusType = dto != null && dto.getSiteBusinessType() != null ? dto.getSiteBusinessType() : null;
		} catch (Exception e) {
			logger.error("获取站点名称失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}

		if (null == siteName) {
			logger.error("没有对应站点");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_SITE_EMPTY);
			return response;
		}
		BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		response.setSiteCode(siteCode);
		response.setSiteName(siteName);
		response.setDmsCode(dmsSiteCode);
		response.setSiteType(siteType);
        response.setOrgId(orgId);
		response.setSiteBusinessType(siteBusType);
        /*
        if(null!=dto) {
            response.setPinyinCode(dto.getSiteNamePym());
        }
        */
		return response;
	}

	@POST
	@Path("/bases/login")
	public BaseResponse login(LoginRequest request) {
		this.logger.info("erpAccount is " + request.getErpAccount());
		this.logger.info("password is " + request.getPassword());

		String erpAccount = request.getErpAccount();
		String erpAccountPwd = request.getPassword();
		/** 进行登录验证 */
		PdaStaff result = baseService.login(erpAccount, erpAccountPwd);

		// 处理返回结果
		if (result.isError()) {
			// 异常处理-验证失败，返回错误信息
			this.logger.info("erpAccount is " + erpAccount + " 验证失败，错误信息[" + result.getErrormsg()
			        + "]");
			// 结果设置
			BaseResponse response = new BaseResponse(JdResponse.CODE_INTERNAL_ERROR,
			        result.getErrormsg());
			// ERP账号
			response.setErpAccount(erpAccount);
			// ERP密码
			response.setPassword(erpAccountPwd);
			// 返回结果
			return response;
		} else {
			// 验证完成，返回相关信息
			this.logger.info("erpAccount is " + erpAccount + " 验证成功");
            try{
                ClientInfo info = null;
                if(StringUtils.isNotBlank(request.getClientInfo())){
                    info = JsonHelper.fromJson(request.getClientInfo(), ClientInfo.class);
                    info.setLoginUserErp(erpAccount);
                }else{
                    info = new  ClientInfo();
                    info.setLoginUserErp(erpAccount);
                }
                sysLoginLogService.insert(result, info);
            }catch (Exception e){
                this.logger.error("用户登录保存日志失败：" + erpAccount, e);
            }
			if (null == result.getSiteId()) {
				BaseResponse response = new BaseResponse(JdResponse.CODE_SITE_ERROR,
				        JdResponse.MESSAGE_SITE_ERROR);
				return response;
			}

			// 结果设置
			BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			// ERP账号
			response.setErpAccount(erpAccount);
			// ERP密码
			response.setPassword(erpAccountPwd);

			// 站点编号
			response.setSiteCode(result.getSiteId());
			// 站点名称
			response.setSiteName(result.getSiteName());
			// 用户ID
			response.setStaffId(result.getStaffId());
			// 用户名称
			response.setStaffName(result.getStaffName());
			response.setOrgId(result.getOrganizationId());
			response.setOrgName(result.getOrganizationName());
			// dmscode
			response.setDmsCode(result.getDmsCod());
			// 返回结果
			return response;
		}
	}

    @GET
	@Path("/bases/drivers/{orgId}")
	public List<BaseResponse> getDrivers(@PathParam("orgId") Integer orgId) {
		// 根据机构ID获取对应的司机信息列表
		this.logger.info("orgId is " + orgId);
		this.logger.info("获取司机信息开始");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();

		BaseStaffSiteOrgDto[] result = null;
		try {
			result = baseService.queryDriverByOrgId(orgId);
		} catch (Exception e) {
			logger.error("获取司机信息失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result) {
			logger.error("获取司机信息失败");
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (result.length == 0) {
			logger.error("获取司机信息为空");
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
	@Path("/bases/allorgs/")
	public List<BaseResponse> getAllOrgs() {
		this.logger.info("获取全部机构信息开始");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();

		List<BaseOrg> result = null;

		try {
			result = baseService.getAllOrg();
		} catch (Exception e) {
			logger.error("获取全部机构信信息失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result || result.size() == 0) {
			logger.error("获取全部机构信息为空");
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

	@GET
	@Path("/bases/sites/{orgId}")
	@GZIP
	public List<BaseResponse> getSites(@PathParam("orgId") Integer orgCode) {
		// 根据机构ID获取对应的站点信息列表
		this.logger.info("orgCode is " + orgCode);
		this.logger.info("获取站点信息开始");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();

		BaseStaffSiteOrgDto[] result = null;
		try {
			result = baseService.querySiteByOrgID(orgCode);
		} catch (Exception e) {
			logger.error("获取站点信息失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result || result.length == 0) {
			logger.error("获取站点信息为空");
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
	public List<BaseResponse> getErrorList() {

		this.logger.info("获取所有错误信息列表");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();
		BaseDataDict[] result = null;
		try {
			result = baseService.getBaseDataDictListByDate(baseSetConfig.getErroral());
		} catch (Exception e) {
			logger.error("获取错误信息列表失败");
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result || result.length == 0) {
			logger.error("获取错误信息为空");
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
    @Path("/bases/getBaseDictionaryTree/{typeGroup}")
    public InvokeResult<List<BaseDataDict>> getBaseDictionaryTree(@PathParam("typeGroup") int typeGroup){
        InvokeResult<List<BaseDataDict>> result=new InvokeResult<List<BaseDataDict>>();
        result.success();
        try{
            result.setData(baseService.getBaseDictionaryTree(typeGroup));
        }catch (Exception ex){
            logger.error(ex.getMessage(),ex);
            result.error(ex);
        }
        return result;
    }

	@GET
	@Path("/bases/error/{typeGroup}")
	public List<BaseResponse> getErrorList(@PathParam("typeGroup") Integer typeGroup) {

		this.logger.info("typeGroup is " + typeGroup);
		this.logger.info("获取错误信息列表");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();
		BaseDataDict[] result = null;
		try {
			result = baseService.getBaseDataDictListByDate(typeGroup);
		} catch (Exception e) {
			logger.error("获取错误信息列表失败");
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result || result.length == 0) {
			logger.error("获取错误信息为空");
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
	public List<BaseResponse> getSiteTypeList() {
		this.logger.info("获取站点类型信息列表");

		List<BaseResponse> ll = new ArrayList<BaseResponse>();
		BaseDataDict[] result = null;
		try {
			result = baseService.getBaseDataDictListByDate(baseSetConfig.getSitetype());
		} catch (Exception e) {
			logger.error("获取站点类型列表失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result || result.length == 0) {
			logger.error("获取站点类型信息为空");
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
	public List<BaseResponse> getSites_New(@PathParam("orgId") Integer orgCode) {
		// 根据机构ID获取对应的站点信息列表
		this.logger.info("orgCode is " + orgCode);
		this.logger.info("获取站点信息开始");

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
			logger.error("获取站点信息失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (null == result || result.length == 0) {
			logger.error("获取站点信息为空");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_SITES_EMPTY);
			ll.add(response);
			return ll;
		}

		dealSites(Arrays.asList(result), ll, true);

		return ll;
	}

	@GET
	//Path("/newbases/allsite/")
	@GZIP
	@Deprecated
	public List<BaseResponse> getDmsSiteAll_New() {
		this.logger.info("查询所有站点信息");
		List<BaseResponse> ll = new ArrayList<BaseResponse>();
		List<BaseStaffSiteOrgDto> results = null;
		try {
			results = baseService.getDmsSiteAll();
		} catch (Exception e) {
			logger.error("查询所有站点信息失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}

		if (results == null || results.size() == 0) {
			logger.info("查询所有站点信息，没有查询到");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_ALLSITE_EMPTY);
			ll.add(response);
			return ll;
		}

		dealSites(results, ll, true);

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
	public BaseResponse getSite_New(@PathParam("code") String code) {
		this.logger.info("sitecode is " + code);

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
			 * this.logger.info("code is siteCode" + code);
			 * param.setSiteCode(NumberHelper.getIntegerValue(code)); }else{
			 * this.logger.info("code is dmsCode" + code);
			 * param.setDmsSiteCode(code); }
			 *
			 * BaseStaffSiteOrgDto[] result =
			 * baseService.selectSiteByPara(param);
			 * this.logger.info("result have" + result.length);
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
			logger.error("获取站点名称失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}

		if (null == siteName) {
			logger.error("没有对应站点");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_SITE_EMPTY);
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
	public List<SysConfigResponse> getConfigByKey(@QueryParam("key") String key) {
		this.logger.info("key is " + key);
		List<SysConfig> configs = baseService.queryConfigByKey(key+"%");

		List<SysConfigResponse> result = new ArrayList<SysConfigResponse>();

		if (configs == null) {
			logger.error("没有对应参数配置");
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
	public void getBaseSite(@PathParam("code") String code) {
		this.logger.info("sitecode is " + code);

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
			this.logger.info("站点类型为" + orderType);
			this.logger.info("baseOrgId" + baseOrgId);
			this.logger.info("baseStoreId" + baseStoreId);
		} else {
			this.logger.info("site is empty");
		}
	}

	@POST
	@Path("/sysconfig")
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
			response.setMessage(SysConfigResponse.MESSAGE_OLD_PASSWORD_ERROR);
			return response;
		} else if (!aSysConfig.getConfigContent().equalsIgnoreCase(sysConfig.getOldPassword())) {
			response.setCode(SysConfigResponse.CODE_OLD_PASSWORD_ERROR);
			response.setMessage(SysConfigResponse.MESSAGE_OLD_PASSWORD_ERROR);
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
	public BaseResponse getselfD(@PathParam("code") Integer code) {
		this.logger.info("sitecode is " + code);

		Integer sitecode = baseService.getSiteSelfDBySiteCode(code);
		BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		response.setSiteCode(sitecode);

		return response;
	}

	@GET
	@Path("/bases/cache/info")
	public Object getCacheInfo() {
		try {
			return cacheMonitor.info();
		} catch (Exception e) {
			return new Integer(-999);
		}
	}

	@GET
	@Path("/bases/cache/memory/{key}")
	public Object getMemoryElement(@PathParam("key") String key) {
		try {
			return cacheMonitor.getMemoryElement(key);
		} catch (Exception e) {
			return "Error";
		}
	}

	@GET
	@Path("/bases/cache/memory/remove/{key}")
	public Object removeMemoryElement(@PathParam("key") String key) {
		try {
			return cacheMonitor.removeMemoryElement(key);
		} catch (Exception e) {
			return "Error";
		}
	}
	
	@GET
	@Path("/bases/capacityTypelist")
	public List<BaseResponse> getCapacityTypelist() {

		//运力编码需求相关类型
		//(7030,2,7030) //线路类型
		//(7031,2,7031) //运力类型
		//(7032,2,7032) //运输方式
		this.logger.info("获取运力的线路类型、运力类型、运输方式 ");

		List<BaseResponse> responseList = new ArrayList<BaseResponse>();
		BaseDataDict[] result = null;
		try {
			//批量提交数据字典信息查询
			result = baseService.getBaseDataDictListByDate(baseSetConfig.getCapacityType());
		} catch (Exception e) {
			//如果异常直接返回
			logger.error("获取获取运力信息列表失败");
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			responseList.add(response);
			return responseList;
		}

		if (null == result || result.length == 0) {
			logger.error("获取运力信息为空");
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
	 * 通过VTS已有的数据字典查询接口中获取对应线路类型、运输方式、承运商类型3个数据字典项的值
	 * 替换之前的通过接口查询青龙基础资料中的数据字典获取线路类型、运输方式、运力类型
	 * add by lhc
	 * 2016.8.31
	 * @return
	 */
	@GET
	@Path("/bases/capacityTypelists")
	public List<BaseResponse> getCapacityTypelists() {
		//运力编码需求相关类型
		//(1011,2,1011) //线路类型：运输类型
		//(1004,2,1004) //运力类型：承运商类型
		//(1001,2,1001) //运输方式 :运输方式
		this.logger.info("获取运力的线路类型、运力类型、运输方式 ");

		List<BaseResponse> responseList = new ArrayList<BaseResponse>();
		DictDto[] result = null;
		try {
			//批量提交数据字典信息查询
			result = baseService.getDictListByGroupType(vtsbaseSetConfig.getCapacityType());
		} catch (Exception e) {
			//如果异常直接返回
			logger.error("获取获取运力信息列表失败");
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			responseList.add(response);
			return responseList;
		}

		if (null == result || result.length == 0) {
			logger.error("获取运力信息为空");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_ERROR_EMPTY);
			responseList.add(response);
			return responseList;
		}

		for (DictDto dto : result) {
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
	public List<BaseResponse> getCarrierInfoList() {
		List<BaseResponse> responseList = new ArrayList<BaseResponse>();
		CarrierParamDto carrierParamDto = new CarrierParamDto();
		carrierParamDto.setOwner("1");
		
		List<CarrierInfo> carrierInfoList = baseService.getCarrierInfoList(carrierParamDto);
		
		if (carrierInfoList != null && carrierInfoList.size() > 0) {
			for (CarrierInfo carrierInfo : carrierInfoList) {
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

	/**
	 *
	 * 分拣细分，分拣中心ID与目的地 获取货柜信息
	 * */
	@GET
	@Path("/bases/goodsposition")
	public ElectronSite getBaseGoodsPositionDmsCodeSiteCode(@QueryParam("createCode") Integer createCode,@QueryParam("receiveCode") Integer receiveCode) {
		return  baseService.getBaseGoodsPositionDmsCodeSiteCode(createCode , receiveCode);

	}

    /**
     * 获取所有签约商家ID列表
     * @return
     */
    @GET
    @Path("/bases/getallsigncustomers")
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
	public DatadictResponse getOrignalBackBusIds(@PathParam("parentGroup")Integer parentGroup
												,@PathParam("nodeLevel") Integer nodeLevel
												,@PathParam("typeGroup") Integer typeGroup){
		logger.info(MessageFormat.format("获取原退商家ID,parentGroup={0} nodeLevel={1} typeGroup={2}"
                , parentGroup, nodeLevel, typeGroup));
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
			logger.error(MessageFormat.format("获取原退商家ID失败，原因{0},堆栈{1}",ex.getMessage(),ex));
			datadictResponse = new DatadictResponse(JdResponse.CODE_INTERNAL_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
			return datadictResponse;
		}catch (Throwable te){
			logger.error(MessageFormat.format("获取原退商家ID失败，原因{0},堆栈{1}", te.getMessage(), te));
			datadictResponse = new DatadictResponse(JdResponse.CODE_INTERNAL_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
			return datadictResponse;
		}
	}

	@GET
	@GZIP
	@Path("/bases/store/{storeType}/{cky2}/{storeID}")
	public WarehouseResponse getWarehouseByCky2(@PathParam("storeType") String storeType
												,@PathParam("cky2") Integer cky2
												, @PathParam("storeID") Integer storeID){
        WarehouseResponse response = new WarehouseResponse();
        PsStoreInfo psStoreInfo = baseMajorManager.getStoreByCky2(storeType, cky2, storeID, DMS);
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
	public SysConfigResponse getSwitchStatus(@PathParam("conName") String conName){
		SysConfigResponse response = new SysConfigResponse();
		List<SysConfig> sysConfigs = null;
		try{
			sysConfigs = sysConfigService.getCachedList(conName);
		}catch(Exception ex){
			logger.error("获取开关信息失败", ex);
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
	public List<BaseResponse> getDmsByOrgId(@PathParam("orgId") Integer orgId){
		this.logger.info("根据orgId获取分拣中心列表,orgId为" + orgId);
		List<BaseResponse> ll = new ArrayList<BaseResponse>();
		List<SimpleBaseSite> dmsList = new ArrayList<SimpleBaseSite>();
		try{
			dmsList = baseMajorManager.getDmsListByOrgId(orgId);
		}catch(Exception e){
			logger.error("根据orgId获取分拣中心列表失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}
		if(null == dmsList || dmsList.size() == 0){
			logger.error("根据orgId获取分拣中心列表为空");
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
	public List<BaseResponse> getDmsAll(){
		this.logger.info("获取所有的分拣中心");
		List<BaseResponse> ll = new ArrayList<BaseResponse>();
		List<BaseStaffSiteOrgDto> baseSiteList = new ArrayList<BaseStaffSiteOrgDto>();
		try{
			baseSiteList = baseMajorManager.getBaseSiteAll();
		}catch(Exception e){
			logger.error("获取所有的分拣中心失败", e);
			BaseResponse response = new BaseResponse(JdResponse.CODE_SERVICE_ERROR,
			        JdResponse.MESSAGE_SERVICE_ERROR);
			ll.add(response);
			return ll;
		}
		// 过滤非分拣中心的数据
		List<BaseStaffSiteOrgDto> dmsList = dmsFilter(baseSiteList);
		if(null == dmsList || dmsList.size() == 0){
			logger.error("获取所有的分拣中心为空");
			BaseResponse response = new BaseResponse(JdResponse.CODE_NOT_FOUND,
			        JdResponse.MESSAGE_SITES_EMPTY);
			ll.add(response);
			return ll;
		}
		for(BaseStaffSiteOrgDto sbs : dmsList){
			BaseResponse br = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			br.setSiteCode(sbs.getSiteCode());
			br.setSiteName(sbs.getSiteName());
			ll.add(br);
		}
		return ll;
	}

	private List<BaseStaffSiteOrgDto> dmsFilter(List<BaseStaffSiteOrgDto> baseSiteList) {
		List<BaseStaffSiteOrgDto> dmsList = new ArrayList<BaseStaffSiteOrgDto>(512);
		if(null != baseSiteList && baseSiteList.size() > 0){
			for(BaseStaffSiteOrgDto site : baseSiteList){
				Integer type = site.getSiteType();
				if(null != type && type == 64){
					dmsList.add(site);
				}
			}
		}
		return dmsList;
	}

    @GET
    @GZIP
    @Path("/bases/getStaffByStaffId/{staffId}")
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
}
