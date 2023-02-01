package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.sysConfig.request.MenuUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.MenuUsageProcessDto;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.ErpLoginServiceManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.AppUpgradeRequest;
import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.request.base.OperateUser;
import com.jd.bluedragon.distribution.api.request.client.DeviceInfo;
import com.jd.bluedragon.distribution.api.request.client.DeviceLocationInfo;
import com.jd.bluedragon.distribution.api.request.client.DeviceLocationUploadPo;
import com.jd.bluedragon.distribution.api.response.AppUpgradeResponse;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.LoginUserResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.ClientRunningModeConfig;
import com.jd.bluedragon.distribution.base.domain.ClientRunningModeConfig.ClientRunningModeConfigItem;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.*;
import com.jd.bluedragon.distribution.client.domain.CheckMenuAuthRequest;
import com.jd.bluedragon.distribution.client.domain.CheckMenuAuthResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.device.service.DeviceLocationService;
import com.jd.bluedragon.distribution.jy.service.config.JyDemotionService;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.sdk.modules.client.DmsClientMessages;
import com.jd.bluedragon.sdk.modules.client.LoginStatusEnum;
import com.jd.bluedragon.sdk.modules.client.LogoutTypeEnum;
import com.jd.bluedragon.sdk.modules.client.ProgramTypeEnum;
import com.jd.bluedragon.sdk.modules.client.dto.*;
import com.jd.bluedragon.utils.*;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.mrd.srv.dto.RpcResultDto;
import com.jd.mrd.srv.service.erp.dto.LoginContextDto;
import com.jd.mrd.srv.service.erp.dto.LoginDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @ClassName: UserService
 * @Description: 用户操作相关的服务实现
 * @author: wuyoude
 * @date: 2018年12月27日 下午3:12:37
 *
 */
@Service
public class UserServiceImpl extends AbstractBaseUserService implements UserService{

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	private static final String DEFAULTTIME = "BasicSystemResource.defaultTime";
	// 下线菜单配置key，分为普通和特殊
	private final String OFFLINE_MENU_CONFIG_KEY_ORDINARY = "ordinary";
	private final String OFFLINE_MENU_CONFIG_KEY_SPECIAL = "special";

	@Autowired
	private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseService baseService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private ErpLoginServiceManager erpLoginServiceManager;

	@Autowired
	private UccPropertyConfiguration uccPropertyConfiguration;

	@Autowired
	private JyDemotionService jyDemotionService;

    @Resource
    private DeviceLocationService deviceLocationService;

	/**
	 * 分拣客户端登录服务
	 * @param request
	 * @return
	 */
    @JProfiler(jKey = "DMS.BASE.UserServiceImpl.dmsClientLogin",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public BaseResponse dmsClientLogin(LoginRequest request){
    	LoginUserResponse loginResponse = this.login(request, LOGIN_TYPE_DMS_CLIENT);
		return loginResponse.toOldLoginResponse();
	}
	/**
	 * 通过jsf调用登录服务
	 * @param request
	 * @return
	 */
    @JProfiler(jKey = "DMS.BASE.UserServiceImpl.oldJsfLogin",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public BaseResponse oldJsfLogin(LoginRequest request){
		LoginUserResponse loginResponse = this.login(request, LOGIN_TYPE_DMS_CLIENT);
		return loginResponse.toOldLoginResponse();
	}

	/**
	 * 通过jsf调用登录服务
	 * @param request
	 * @return
	 */
    @JProfiler(jKey = "DMS.BASE.UserServiceImpl.jsfLogin",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public LoginUserResponse jsfLogin(LoginRequest request){
    	//安卓pda登录，不校验版本号并设置默认底包版本
    	if(request != null){
    		request.setCheckVersion(Boolean.FALSE);
    		request.setBaseVersionCode(JSF_LOGIN_DEFAULT_BASE_VERSION_CODE);
    	}
		return clientLoginIn(request);
	}

    /**
     * 客户端登录获取登录信息接口(安卓PDA)，增加token信息
     *
     * @param request
     * @return
     * @author fanggang7
     * @time 2021-03-09 19:32:02 周二
     */
    @Override
    public LoginUserResponse jsfLoginWithToken(LoginRequest request) {
        //安卓pda登录，不校验版本号并设置默认底包版本
        if (request != null) {
            request.setCheckVersion(Boolean.FALSE);
            request.setBaseVersionCode(JSF_LOGIN_DEFAULT_BASE_VERSION_CODE);
        }
        LoginUserResponse loginUserResponse = clientLoginIn(request);
        this.getAndSaveToken(request, loginUserResponse);
        this.handleDeviceLocation(request, loginUserResponse);
        return loginUserResponse;
    }

    private boolean getAndSaveToken(LoginRequest request, LoginUserResponse loginUserResponse) {
        try {
            if (loginUserResponse == null || !Objects.equals(loginUserResponse.getCode(), JdResponse.CODE_OK)) {
                return false;
            }
            if(request == null || StringUtils.isEmpty(request.getClientInfo())){
                loginUserResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                loginUserResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return false;
            }
            ClientInfo clientInfo = JsonHelper.fromJson(request.getClientInfo(), ClientInfo.class);
            if(clientInfo == null){
                loginUserResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                loginUserResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return false;
            }
            String deviceId = clientInfo.getDeviceId();
            if(StringUtils.isBlank(deviceId)){
                loginUserResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                loginUserResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return false;
            }
            String token = UUID.randomUUID().toString();
            loginUserResponse.setToken(token);
            // 保存缓存
            String clientLoginDeviceIdKey = String.format(CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_LOGIN_USER_DEVICE_ID, request.getErpAccount(), deviceId);
            jimdbCacheService.setEx(clientLoginDeviceIdKey, token, CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_LOGIN_DEVICE_ID_EXPIRE_TIME, TimeUnit.HOURS);

            // 物流网关外网erp认证登录
            this.wlGwErpLogin(request, clientInfo, loginUserResponse);
        } catch (Exception e) {
            log.error("UserServiceImpl.getAndSaveToken exception ", e);
            loginUserResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            loginUserResponse.setMessage("保存登录token数据异常");
            return false;
        }
        return true;
    }

    private void wlGwErpLogin(LoginRequest request, ClientInfo clientInfo, LoginUserResponse loginUserResponse) {
        try {
            // 物流网关外网erp认证登录
            LoginDto loginDto = new LoginDto();
            loginDto.setAppCode("dmsPdaAndroid");
            loginDto.setUserName(loginUserResponse.getErpAccount());
            loginDto.setSource("erp");
            loginDto.setTicketType("long");
            loginDto.setClientVersion(clientInfo.getVersionCode());
            loginDto.setDeviceId(clientInfo.getDeviceId());
            loginDto.setDeviceType(clientInfo.getVersionName());
            loginDto.setDeviceName(clientInfo.getVersionName());
            loginDto.setIp("127.0.0.1");
            loginDto.setPwd(Encrypt.encodeDes(request.getPassword()));
            final RpcResultDto<LoginContextDto> loginResult = erpLoginServiceManager.loginSecurity(loginDto);
            log.info("wlGwErpLogin loginSecurity param {} result {}", JsonHelper.toJson(loginDto), JsonHelper.toJson(loginResult));
            if(!loginResult.isSuccess()){
                log.warn("wlGwErpLogin loginSecurity fail param {} result {}", JsonHelper.toJson(loginDto), JsonHelper.toJson(loginResult));
                return;
            }
            loginUserResponse.setWlGwTicket(loginResult.getData().getTicket());
        } catch (Exception e) {
            log.error("wlGwErpLogin exception ", e);
        }
    }

    private void handleDeviceLocation(LoginRequest loginRequest, LoginUserResponse loginUserResponse) {
        try {
            log.info("UserServiceImpl.handleDeviceLocation param {}", JsonHelper.toJson(loginRequest));
            if (loginUserResponse == null || !Objects.equals(loginUserResponse.getCode(), JdResponse.CODE_OK)) {
                return;
            }
            DeviceInfo deviceInfo = loginRequest.getDeviceInfo();
            final DeviceLocationInfo deviceLocationInfo = loginRequest.getDeviceLocationInfo();
            if (deviceInfo == null) {
                deviceInfo = new DeviceInfo();
                log.warn("UserServiceImpl.handleDeviceLocation deviceInfo null {}", JsonHelper.toJson(loginRequest));
                // return;
            }

            DeviceLocationUploadPo deviceLocationUploadPo = new DeviceLocationUploadPo();
            deviceLocationUploadPo.setDeviceLocationInfo(deviceLocationInfo);
            deviceLocationUploadPo.setDeviceInfo(deviceInfo);

            long operateTime = new Date().getTime();
            final Date operateTimeDate = DateUtil.parse(loginRequest.getOperateTime(), DateUtil.FORMAT_DATE_TIME);
            if (operateTimeDate != null) {
                operateTime = operateTimeDate.getTime();
            }
            deviceLocationUploadPo.setOperateTime(operateTime);

            final OperateUser operateUser = new OperateUser();
            operateUser.setUserCode(loginUserResponse.getErpAccount());
            operateUser.setUserName(loginUserResponse.getStaffName());
            operateUser.setOrgId(loginUserResponse.getOrgId());
            operateUser.setOrgName(loginUserResponse.getOrgName());
            operateUser.setSiteCode(loginUserResponse.getSiteCode());
            operateUser.setSiteName(loginUserResponse.getSiteName());
            deviceLocationUploadPo.setOperateUser(operateUser);
            deviceLocationService.sendUploadLocationMsg(deviceLocationUploadPo);
        } catch (Exception e) {
            log.error("UserServiceImpl.handleDeviceLocation exception {} {}", JsonHelper.toJson(loginRequest), JsonHelper.toJson(loginUserResponse), e);
        }
    }

    /**
     * 客户端登录token验证
     *
     * @return
     * @author fanggang7
     * @time 2021-03-09 19:32:02 周二
     */
    @Override
    public JdResult<Boolean> verifyClientLoginToken(String userErp, String deviceId, String token) {
        log.info("UserServiceImpl.verifyClientLoginToken userErp {}, deviceId {}, token {}", userErp, deviceId, token);
        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess("success");
        try {
            if(StringUtils.isBlank(userErp) || StringUtils.isBlank(deviceId) || StringUtils.isBlank(token)){
                result.setCode(JdResponse.CODE_PARAM_ERROR);
                result.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return result;
            }
            String clientLoginDeviceIdKey = String.format(CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_LOGIN_USER_DEVICE_ID, userErp, deviceId);
            String tokenExistVal = jimdbCacheService.get(clientLoginDeviceIdKey);
            log.info("UserServiceImpl.verifyClientLoginToken tokenExistVal {}", tokenExistVal);
            if(!Objects.equals(token, tokenExistVal)){
                result.toFail("token不合法");
                return result;
            }
        } catch (Exception e) {
            log.error("UserServiceImpl.verifyClientLoginToken exception ", e);
            result.toFail("验证登录信息异常 " + e.getMessage());
            return result;
        }
        return result;
    }

	@Override
	public JdResult<LoginUserResponse> getLoginUser(LoginRequest request) {
		JdResult<LoginUserResponse> loginResult = new JdResult<LoginUserResponse>();
		loginResult.toSuccess("获取登录账户信息成功！");
		if(request == null || StringHelper.isEmpty(request.getErpAccount())){
			loginResult.toFail("登录账号不能为空！");
		}else{
			String userErp = request.getErpAccount();
			BaseStaffSiteOrgDto basestaffDto = baseMajorManager.getBaseStaffByErpNoCache(userErp);
	        if (null == basestaffDto) {
	        	loginResult.toFail("账号"+userErp+"在青龙基础资料中未维护！");
	        } else if(basestaffDto.getSiteCode() == null 
	        		|| basestaffDto.getSiteCode() <= 0){
	        	loginResult.toFail("账号"+userErp+"在青龙基础资料中未维护站点信息！");
	        }else{	
	        	LoginUserResponse loginUserResponse = new LoginUserResponse();
	    		// 用户ID
	        	loginUserResponse.setStaffId(basestaffDto.getStaffNo());
	    		// 用户名称
	        	loginUserResponse.setStaffName(basestaffDto.getStaffName());
	        	//erp
	        	loginUserResponse.setErpAccount(userErp);
	    		// 分拣中心ID
	        	BaseStaffSiteOrgDto siteInfo = this.baseMajorManager.getBaseSiteBySiteId(basestaffDto.getSiteCode());
	        	if(siteInfo != null){
	        		loginUserResponse.setSiteCode(siteInfo.getSiteCode());
		    		// 分拣中心名称
		        	loginUserResponse.setSiteName(siteInfo.getSiteName());
		    		// 机构ID
		        	loginUserResponse.setOrgId(siteInfo.getOrgId());
		    		// 机构名称
		        	loginUserResponse.setOrgName(siteInfo.getOrgName());
		    		// DMS编码
		        	loginUserResponse.setDmsCode(siteInfo.getDmsSiteCode());
		    		// 站点类型
		        	loginUserResponse.setSiteType(siteInfo.getSiteType());
		            // 站点子类型
		        	loginUserResponse.setSubType(siteInfo.getSubType());
					loginUserResponse.setDmsSiteCode(siteInfo.getSiteCode());
					loginUserResponse.setDmsSiteName(siteInfo.getSiteName());
		        	//设置分拣中心信息(同打印客户端逻辑)
		        	if(siteInfo.getDmsId() != null && siteInfo.getDmsId() > 0){
		        		loginUserResponse.setDmsSiteCode(siteInfo.getDmsId());
		        		loginUserResponse.setDmsSiteName(siteInfo.getDmsName());
		        	}
		        	this.setRunningMode(request, loginUserResponse);
	        		loginResult.setData(loginUserResponse);
	        	}else{
	        		loginResult.toFail("账号"+userErp+"对应的站点"+basestaffDto.getSiteCode()+"无效！");
	        	}
	        }
		}
		return loginResult;
	}
	/**
	 * 搜集客户端心跳信息，控制客户端退出、更新版本等
	 */
	@Override
	public JdResult<DmsClientHeartbeatResponse> sendHeartbeat(DmsClientHeartbeatRequest dmsClientHeartbeatRequest){
		//调用jsf记录心跳信息
		JdResult<DmsClientHeartbeatResponse> result = dmsClientManager.sendHeartbeat(dmsClientHeartbeatRequest);
		if(result != null 
				&& result.isSucceed()
				&& result.getData() != null){
			Integer programType = result.getData().getProgramType();
			String userCode = result.getData().getUserCode();
			//设置运行环境信息，后续迁移到business
			if(result.getData().getDmsClientConfigInfo() != null){
				Integer siteCode = NumberHelper.convertToInteger(result.getData().getSiteCode());
				Integer orgCode = NumberHelper.convertToInteger(result.getData().getOrgCode());
				String runningMode = this.getRunningMode(programType, userCode, siteCode, orgCode);
				if(StringHelper.isNotEmpty(runningMode)){
					result.getData().getDmsClientConfigInfo().setRunningMode(runningMode);
				}
			}
			//PDA登录，特殊控制处理
			if(ProgramTypeEnum.isPda(programType)){
				//校验校验客户端时间和服务器时间差异
	            if(!checkClientTime(dmsClientHeartbeatRequest.getRequestTime(),result.getData().getServerTime())){
	            	result.getData().setForceLogout(Boolean.TRUE);
	            	result.getData().setLogoutType(LogoutTypeEnum.WARN_CLIENT_TIME_INVALID.getTypeCode());
	            	result.toWarn(JdResponse.CODE_TIMEOUT,JdResponse.MESSAGE_TIMEOUT + "[PDA时间:" + dmsClientHeartbeatRequest.getRequestTime() + ",服务器时间:" + result.getData().getServerTime() + "]");
	                return result;
	            }
				//校验是否重复登录
				if(LoginStatusEnum.WARN_MULTIPLE_LOGIN.getStatusCode().equals(result.getData().getLoginStatus())
						&& sysConfigService.getConfigByName(SysConfigService.SYS_CONFIG_PDA_CHECK_MULTIPLE_LOGIN)){
					result.getData().setForceLogout(Boolean.TRUE);
					result.getData().setLogoutType(LogoutTypeEnum.WARN_MULTIPLE_LOGIN.getTypeCode());
					result.toWarn(DmsClientMessages.WARN_NEED_LOGOUT.getMsgCode(),"该账号已在其他设备登录！");
					return result;
				}
				//校验账号是否有效
				if(!checkUserCode(userCode)){
					result.getData().setForceLogout(Boolean.TRUE);
					result.getData().setLogoutType(LogoutTypeEnum.WARN_USER_INVALID.getTypeCode());
	                result.toWarn(JdResponse.CODE_RESIGNATION,JdResponse.MESSAGE_RESIGNATION);
	                return result;
	            }
				// 获取转运app全局降级配置
				if(Objects.equals(ProgramTypeEnum.PDA_ANDROID.getCode(), programType)){
					BusinessConfigInfo businessConfigInfo = new BusinessConfigInfo();
					businessConfigInfo.setJyDemotionConfigList(jyDemotionService.obtainJyDemotionConfig());
					result.getData().setBusinessConfigInfo(businessConfigInfo);
				}
            }
		}
		return result;
	}
    /**
     * 校验账号是否有效
     * @param userCode
     * @return
     */
    public boolean checkUserCode(String userCode){
		if(userCode != null && !userCode.toLowerCase().contains(Constants.PDA_THIRDPL_TYPE)){
            BaseStaffSiteOrgDto baseDto = baseMajorManager.getBaseStaffIgnoreIsResignByErp(userCode);
            if(baseDto == null || baseDto.getIsResign() == null || baseDto.getIsResign() != 1){
                return false;
            }
		}
        return true;
    }
    /**
     * 校验客户端时间和服务器时间，不能相差5分钟
     * @param clientTime
     * @param serverTime
     * @return
     */
    private boolean checkClientTime(Date clientTime, Date serverTime){
    	//判断为空，返回false
    	if(clientTime == null || serverTime == null){
    		return false;
    	}
        long diff = Math.abs(clientTime.getTime() - serverTime.getTime());
        String defaultTime = PropertiesHelper.newInstance().getValue(DEFAULTTIME);
        if(diff/(1000*60) < Integer.valueOf(defaultTime)){
            return true;
        }
        return false;
    }
	/**
	 * 根据配置设置当前登录人的环境
	 * @param request
	 * @param loginUserResponse
	 */
	private void setRunningMode(LoginRequest request,LoginUserResponse loginUserResponse){
		Integer programType = null;
		String userCode = null;
		Integer siteCode = null;
		Integer orgCode = null;
		//获取客户端配置信息
		if(StringUtils.isNotBlank(request.getClientInfo())){
			ClientInfo clientInfo = JsonHelper.fromJson(request.getClientInfo(), ClientInfo.class);
			if(clientInfo != null){
				programType = clientInfo.getProgramType();
			}
		}
		if(loginUserResponse != null){
			userCode = loginUserResponse.getErpAccount();
			siteCode = loginUserResponse.getSiteCode();
			orgCode = loginUserResponse.getOrgId();
		}
		String runningMode = this.getRunningMode(programType, userCode, siteCode, orgCode);
		if(loginUserResponse != null && StringHelper.isNotEmpty(runningMode)){
			loginUserResponse.setRunningMode(runningMode);
		}
	}
	/**
	 * 获取当前环境配置信息（后续改为jsf心跳接口返回）
	 * @param programType
	 * @param userCode
	 * @param siteCode
	 * @param orgCode
	 * @return
	 */
	private String getRunningMode(Integer programType,String userCode,Integer siteCode,Integer orgCode){
		String runningMode = null;
		if(programType != null){
			//监控
			CallerInfo callerInfo = ProfilerHelper.registerInfo(String.format(UmpConstants.UMP_KEY_FORMAT_REST_CLIENT_GET_RUNNING_MODE,programType,this.runningMode));
			Profiler.registerInfoEnd(callerInfo);
	    	//1、查询客户端环境配置信息
	    	SysConfig checkConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_CLIENT_RUNNING_MODE_PRE+programType);
	    	if(checkConfig!=null && StringHelper.isNotEmpty(checkConfig.getConfigContent())){
	    		ClientRunningModeConfig clientRunningModeConfig = JsonHelper.fromJson(checkConfig.getConfigContent(), ClientRunningModeConfig.class);
	    		if(clientRunningModeConfig != null){
	    			runningMode = clientRunningModeConfig.getDefaultRunningMode();
	    			//2、分别按erp、站点、机构逐级判断是否匹配
	    			if(clientRunningModeConfig.getConfigItems() != null && !clientRunningModeConfig.getConfigItems().isEmpty()){
	    				for(ClientRunningModeConfigItem item:clientRunningModeConfig.getConfigItems()){
	    					if(item.getUserErps() != null && item.getUserErps().contains(userCode)){
	    						runningMode = item.getRunningMode();
	    						break;
	    					}
	    					if(item.getSiteCodes() != null && item.getSiteCodes().contains(siteCode)){
	    						runningMode = item.getRunningMode();
	    						break;
	    					}
	    					if(item.getOrgCodes() != null && item.getOrgCodes().contains(orgCode)){
	    						runningMode = item.getRunningMode();
	    						break;
	    					}
	    				}
	    			}
	    		}
	    	}
		}
		return runningMode;
	}
	@Override
	public String getServerRunningMode() {
		return this.runningMode;
	}

	@Override
	protected LoginClientService selectLoginClient() {
		return (LoginClientService) baseService;
	}
	@Override
	public JdResult<CheckMenuAuthResponse> checkMenuAuth(CheckMenuAuthRequest checkMenuAuthRequest) {
		JdResult<CheckMenuAuthResponse> checkResult = new JdResult<CheckMenuAuthResponse>();
		checkResult.toSuccess();
		if(checkMenuAuthRequest == null
				|| StringHelper.isEmpty(checkMenuAuthRequest.getMenuCode())
				|| checkMenuAuthRequest.getSiteType() == null) {
			checkResult.toFail("请求参数无效！缺少menuCode和siteType");
			return checkResult;
		}
		CheckMenuAuthResponse respData = new CheckMenuAuthResponse();
		checkResult.setData(respData);
		// 菜单下线提示
		if(checkMenuIsOffline(checkMenuAuthRequest.getMenuCode(), checkResult)){
			return checkResult;
		}
		if(BusinessHelper.isSmsZzgztSite(checkMenuAuthRequest.getSiteType(),checkMenuAuthRequest.getSiteSubType())) {
	        //1、查询站点黑名单菜单编码
	        List<String> smsSiteMenuBlacklist = sysConfigService.getStringListConfig(Constants.SYS_CONFIG_CLIENT_SMSSITEMENUBLACKLIST);
	        if(smsSiteMenuBlacklist.contains(checkMenuAuthRequest.getMenuCode())) {
	        	checkResult.toFail(HintService.getHint(HintCodeConstants.PRINT_CLINET_SMS_SITE_MENU_NOAUTH));
	        	return checkResult;
	        }
		}
		//菜单权限验证，按场地限制
        MenuUsageConfigRequestDto menuUsageConfigRequestDto = new MenuUsageConfigRequestDto();
        menuUsageConfigRequestDto.setMenuCode(checkMenuAuthRequest.getMenuCode());
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(checkMenuAuthRequest.getSiteCode());
        menuUsageConfigRequestDto.setCurrentOperate(currentOperate);
        User user = new User();
        user.setUserErp(checkMenuAuthRequest.getUserCode());
        menuUsageConfigRequestDto.setUser(user);
        MenuUsageProcessDto clientMenuUsageConfig = baseService.getClientMenuUsageConfig(menuUsageConfigRequestDto);
        if(clientMenuUsageConfig != null) {
        	respData.setCanUse(clientMenuUsageConfig.getCanUse());
        	respData.setMsg(clientMenuUsageConfig.getMsg());
        	respData.setMsgType(clientMenuUsageConfig.getMsgType());
        	//菜单不可用
        	if(Objects.equals(Constants.YN_NO, clientMenuUsageConfig.getCanUse())){
        		checkResult.toFail(clientMenuUsageConfig.getMsg());
	        	return checkResult; 
        	}
        }
		return checkResult;
	}

	private boolean checkMenuIsOffline(String menuCode, JdResult<CheckMenuAuthResponse> result) {
		Map<String, Map<String, Object>> stringMapMap = com.jd.bluedragon.utils.JsonHelper.json2Map(uccPropertyConfiguration.getClientOfflineMenuConfig());
		if(stringMapMap == null){
			return false;
		}
		Map<String, Object> commonMap = stringMapMap.get(OFFLINE_MENU_CONFIG_KEY_ORDINARY);
		for (Map.Entry<String, Object> entry : commonMap.entrySet()) {
			if(Arrays.asList(entry.getKey().split(Constants.SEPARATOR_COMMA)).contains(menuCode)){
				result.toFail(String.valueOf(entry.getValue()));
				return true;
			}
		}
		Map<String, Object> specialMap = stringMapMap.get(OFFLINE_MENU_CONFIG_KEY_SPECIAL);
		if(specialMap.containsKey(menuCode)){
			result.toFail(String.valueOf(specialMap.get(menuCode)));
			return true;
		}
		return false;
	}

	@Override
    @JProfiler(jKey = "DMS.BASE.UserServiceImpl.checkAppVersion",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdResult<AppUpgradeResponse> checkAppVersion(AppUpgradeRequest request) {
        JdResult<AppUpgradeResponse> result = new JdResult<>();
        result.toSuccess();

        AppUpgradeResponse response = new AppUpgradeResponse();
        response.setNeedUpdate(false); // 默认不升级
        result.setData(response);

        if (StringUtils.isBlank(request.getVersionCode())) {
            log.warn("检测升级缺少版本号:{}", JsonHelper.toJson(request));
            return result;
        }

        try {
            DmsClientVersionRequest versionRequest = assembleVersionRequest(request);

            versionCompare(request, response, versionRequest);
        }
        catch (Exception e) {
            log.error("APP版本检测异常. {}", JsonHelper.toJson(request), e);
            result.toError("APP版本检测异常");
        }

        return result;
    }

    /**
     * 版本比较
     * @param request
     * @param response
     * @param versionRequest
     */
    private void versionCompare(AppUpgradeRequest request, AppUpgradeResponse response, DmsClientVersionRequest versionRequest) {
        JdResult<DmsClientVersionResponse> clientResponse = dmsClientManager.getClientVersion(versionRequest);
        if (clientResponse.isSucceed() && null != clientResponse.getData()) {
            DmsClientVersionResponse clientVersion = clientResponse.getData();
            DmsClientConfigInfo dmsClientConfigInfo = clientVersion.getDmsClientConfigInfo();
            if (dmsClientConfigInfo != null && StringUtils.isNotBlank(dmsClientConfigInfo.getVersionCode())) {
                boolean needUpgrade = BusinessUtil.appVersionCompare(request.getVersionCode(), dmsClientConfigInfo.getVersionCode());

                if (log.isInfoEnabled()) {
                    log.info("version compare: old:{}, newest:{}, result:{}", request.getVersionCode(), dmsClientConfigInfo.getVersionCode(), needUpgrade);
                }

                setUpgradeResponse(response, dmsClientConfigInfo, needUpgrade);
            }
        }
    }

    private void setUpgradeResponse(AppUpgradeResponse response, DmsClientConfigInfo dmsClientConfigInfo, boolean needUpgrade) {
        if (needUpgrade) {
            response.setNeedUpdate(true);
        }

        response.setDownloadType(dmsClientConfigInfo.getDownloadType());
        response.setDownloadUrl(dmsClientConfigInfo.getDownloadUrl());
        response.setFileName(dmsClientConfigInfo.getFileName());
        response.setFileSize(dmsClientConfigInfo.getFileSize());
        response.setFileCheckCode(dmsClientConfigInfo.getFileCheckCode());
        response.setVersionRemark(dmsClientConfigInfo.getVersionRemark());
        response.setFileItemsCheckCode(dmsClientConfigInfo.getFileItemsCheckCode());
        response.setRunningMode(dmsClientConfigInfo.getRunningMode());
        response.setVersionCode(dmsClientConfigInfo.getVersionCode());
    }

    private DmsClientVersionRequest assembleVersionRequest(AppUpgradeRequest request) {
        DmsClientVersionRequest versionRequest = new DmsClientVersionRequest();
        versionRequest.setProgramType(ANDROID_LOGIN_PROGRAM_TYPE);
        versionRequest.setCurrentVersionCode(request.getVersionCode());
        versionRequest.setSystemCode(Constants.SYS_CODE_DMS);

        if (NumberHelper.gt0(request.getSiteCode())) {
            versionRequest.setSiteCode(String.valueOf(request.getSiteCode()));

            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(request.getSiteCode());
            if (null != baseSite) {
                versionRequest.setOrgCode(String.valueOf(baseSite.getOrgId()));
                versionRequest.setOrgName(baseSite.getOrgName());
                versionRequest.setSiteName(baseSite.getSiteName());
            }
        }
        if (StringUtils.isNotBlank(request.getErpAccount())) {
            versionRequest.setUserCode(request.getErpAccount());
        }
        if (StringUtils.isNotBlank(request.getDeviceId())) {
            versionRequest.setMachineCode(request.getDeviceId());
        }

        return versionRequest;
    }
}
