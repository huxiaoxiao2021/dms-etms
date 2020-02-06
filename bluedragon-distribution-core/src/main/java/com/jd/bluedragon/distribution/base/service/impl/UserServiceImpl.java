package com.jd.bluedragon.distribution.base.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.LoginUserResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.ClientRunningModeConfig;
import com.jd.bluedragon.distribution.base.domain.ClientRunningModeConfig.ClientRunningModeConfigItem;
import com.jd.bluedragon.distribution.base.domain.LoginCheckConfig;
import com.jd.bluedragon.distribution.base.domain.PdaStaff;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.base.service.UserService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.bluedragon.distribution.sysloginlog.domain.SysLoginLog;
import com.jd.bluedragon.distribution.sysloginlog.service.SysLoginLogService;
import com.jd.bluedragon.distribution.version.domain.ClientConfig;
import com.jd.bluedragon.distribution.version.service.ClientConfigService;
import com.jd.bluedragon.sdk.modules.client.DmsClientMessages;
import com.jd.bluedragon.sdk.modules.client.LoginStatusEnum;
import com.jd.bluedragon.sdk.modules.client.LogoutTypeEnum;
import com.jd.bluedragon.sdk.modules.client.ProgramTypeEnum;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientHeartbeatRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientHeartbeatResponse;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLoginRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLoginResponse;
import com.jd.bluedragon.service.remote.client.DmsClientManager;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

/**
 * 
 * @ClassName: UserService
 * @Description: 用户操作相关的服务实现
 * @author: wuyoude
 * @date: 2018年12月27日 下午3:12:37
 *
 */
@Service
public class UserServiceImpl implements UserService{

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	private static final String DEFAULTTIME = "BasicSystemResource.defaultTime";
	 
	/**
	 *	登录方式-分拣客户端（PDA、打印、标签设计器）
	 */
	private static final Integer LOGIN_TYPE_DMS_CLIENT = 1;
	/**
	 *  登录方式-jsf（安卓版PDA）
	 */
	private static final Integer LOGIN_TYPE_JSF = 2;
	/**
	 *  程序类型-jsf登录接口
	 */
	private static final Integer JSF_LOGIN_PROGRAM_TYPE = 50;

	/**
	 *  程序类型-安卓登录接口
	 */
	private static final Integer ANDROID_LOGIN_PROGRAM_TYPE = 60;

	/**
	 *  默认版本号-jsf登录接口
	 */
	private static final String JSF_LOGIN_DEFAULT_VERSION_CODE = "20180101JSF";
	/**
	 *  默认版本名称-jsf登录接口
	 */
	private static final String JSF_LOGIN_DEFAULT_VERSION_NAME = "Jsf_Login";
	/**
	 * 当前应用的环境（prod-全国 pre-华中 uat-UAT test-测试）
	 */
	@Value("${app.config.runningMode:prod}")
	private String runningMode;
	@Autowired
	private BaseService baseService;
	@Autowired
	private SysConfigService sysConfigService;
	@Autowired
	private SysLoginLogService sysLoginLogService;
	@Autowired
	private ClientConfigService clientConfigService;
	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	@Qualifier("basicPrimaryWS")
	private BasicPrimaryWS basicPrimaryWS;
	@Autowired
	private DmsClientManager dmsClientManager;
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
    @JProfiler(jKey = "DMS.BASE.UserServiceImpl.jsfLogin",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public BaseResponse jsfLogin(LoginRequest request){
		LoginUserResponse loginResponse = this.login(request, LOGIN_TYPE_DMS_CLIENT);
		return loginResponse.toOldLoginResponse();
	}

	@JProfiler(jKey = "DMS.BASE.UserServiceImpl.clientLoginIn", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public LoginUserResponse clientLoginIn(LoginRequest request) {
    	if (null != request) {
    		request.setLoginVersion((byte)1);
		}
    	LoginUserResponse response = this.login(request, LOGIN_TYPE_DMS_CLIENT);
		if (response.getCode().equals(JdResponse.CODE_OK)) {
			this.bindSite2LoginUser(response);
		}
		return response;
	}

	/**
	 *
	 * @param response
	 */
	private void bindSite2LoginUser(LoginUserResponse response) {
		response.setDmsId(response.getSiteCode());
		response.setDmsName(response.getSiteName());
		// 非分拣中心类型的站点查询分拣中心ID和名称，兼容打印客户端登录后再查询站点的逻辑
		if (!Constants.DMS_SITE_TYPE.equals(response.getSiteType())) {
			BaseStaffSiteOrgDto dtoStaff = basicPrimaryWS.getBaseSiteBySiteId(response.getSiteCode());
			if (null != dtoStaff) {
				response.setSiteType(dtoStaff.getSiteType());
				response.setSubType(dtoStaff.getSubType());
				if (!dtoStaff.getDmsId().equals(dtoStaff.getSiteCode()) && dtoStaff.getDmsId() > 0) {
					response.setDmsId(dtoStaff.getDmsId());
					response.setDmsName(dtoStaff.getDmsName());
				}
				if (log.isInfoEnabled()) {
					log.info("set sortingCenter and type, response:[{}]", response.toString());
				}
			}
		}
	}

	/**
	 * 分拣客户端登录入口
	 * @param request 登录请求
	 * @param loginType 登录方式
	 * @return
	 */
	private LoginUserResponse login(LoginRequest request,Integer loginType) {
		log.info("erpAccount is {}" , request.getErpAccount());

		String erpAccount = request.getErpAccount();
		String erpAccountPwd = request.getPassword();
		ClientInfo clientInfo = null;
		Long loginId = 0L;
		//初始化客户端信息
		if(StringUtils.isNotBlank(request.getClientInfo())){
			clientInfo = JsonHelper.fromJson(request.getClientInfo(), ClientInfo.class);
		}else if(LOGIN_TYPE_JSF.equals(loginType)){
			//jsf方式登录未传入客户端信息时，自动设置客户端信息
			clientInfo = new  ClientInfo();
			clientInfo.setProgramType(JSF_LOGIN_PROGRAM_TYPE);
			clientInfo.setVersionCode(JSF_LOGIN_DEFAULT_VERSION_CODE);
			clientInfo.setVersionName(JSF_LOGIN_DEFAULT_VERSION_NAME);
		}else{
			clientInfo = new  ClientInfo();
		}
		clientInfo.setLoginUserErp(erpAccount);
		/** 进行登录验证 */
		PdaStaff loginResult = baseService.login(erpAccount, erpAccountPwd, clientInfo, request.getLoginVersion());

		// 处理返回结果
		if (loginResult.isError()) {
			// 异常处理-验证失败，返回错误信息
			log.info("erpAccount is {} 验证失败，错误信息[{}]",erpAccount, loginResult.getErrormsg());
			// 结果设置
			LoginUserResponse response = new LoginUserResponse(JdResponse.CODE_INTERNAL_ERROR,
			        loginResult.getErrormsg());
			// ERP账号
			response.setErpAccount(erpAccount);
			// ERP密码
			response.setPassword(erpAccountPwd);
			// 返回结果
			return response;
		} else {
			// 验证完成，返回相关信息
			log.info("erpAccount is {} 验证成功",erpAccount);
			try{
				//检查客户端版本信息，版本不一致，不允许登录
	            JdResult<String> checkResult = checkClientInfo(clientInfo,loginResult);
	            if(!checkResult.isSucceed()){
	            	clientInfo.setMatchFlag(SysLoginLog.MATCHFLAG_LOGIN_FAIL);
	            	sysLoginLogService.insert(loginResult, clientInfo);
	            	log.warn("login-fail:params={},msg={}", JsonHelper.toJson(request), checkResult.getMessage());
					LoginUserResponse response = new LoginUserResponse(JdResponse.CODE_INTERNAL_ERROR,
							checkResult.getMessage());
					// ERP账号
					response.setErpAccount(erpAccount);
					// ERP密码
					response.setPassword(erpAccountPwd);
					// 返回结果
					return response;
				}else{
					sysLoginLogService.insert(loginResult, clientInfo);
					//jsf保存登录记录接口
					DmsClientLoginRequest dmsClientLoginRequest = new DmsClientLoginRequest();
					dmsClientLoginRequest.setProgramType(clientInfo.getProgramType());
					dmsClientLoginRequest.setVersionCode(clientInfo.getVersionCode());
					dmsClientLoginRequest.setSystemCode(Constants.SYS_CODE_DMS);
					String orgCode = null;
					if(loginResult.getOrganizationId() != null){
						orgCode = loginResult.getOrganizationId().toString();
					}
					dmsClientLoginRequest.setOrgCode(orgCode);
					dmsClientLoginRequest.setOrgName(loginResult.getOrganizationName());
					String siteCode = null;
					if(loginResult.getSiteId() != null){
						siteCode = loginResult.getSiteId().toString();
					}
					dmsClientLoginRequest.setSiteCode(siteCode);
					dmsClientLoginRequest.setSiteName(loginResult.getSiteName());
					dmsClientLoginRequest.setUserCode(request.getErpAccount());
					dmsClientLoginRequest.setUserName(loginResult.getStaffName());
					
					dmsClientLoginRequest.setMacAdress(clientInfo.getMacAdress());
					dmsClientLoginRequest.setMachineCode(clientInfo.getMachineName());
					dmsClientLoginRequest.setIpv4(clientInfo.getIpv4());
					dmsClientLoginRequest.setIpv6(clientInfo.getIpv6());
					JdResult<DmsClientLoginResponse> loginResponse = dmsClientManager.login(dmsClientLoginRequest);
					if(loginResponse != null 
							&& loginResponse.isSucceed()
							&& loginResponse.getData() != null){
						loginId = loginResponse.getData().getLoginId();
					}
				}
	        }catch (Exception e){
	            log.error("用户登录保存日志失败：{}", erpAccount, e);
	        }
			if (null == loginResult.getSiteId()) {
				return new LoginUserResponse(JdResponse.CODE_SITE_ERROR, JdResponse.MESSAGE_SITE_ERROR);
			}

			// 结果设置
			LoginUserResponse response = new LoginUserResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			// ERP账号
			response.setErpAccount(erpAccount);
			// ERP密码
			response.setPassword(erpAccountPwd);

			// 站点编号
			response.setSiteCode(loginResult.getSiteId());
			// 站点名称
			response.setSiteName(loginResult.getSiteName());
			// 用户ID
			response.setStaffId(loginResult.getStaffId());
			// 用户名称
			response.setStaffName(loginResult.getStaffName());
			response.setOrgId(loginResult.getOrganizationId());
			response.setOrgName(loginResult.getOrganizationName());
			// 站点类型
			response.setSiteType(loginResult.getSiteType());
			//站点子类型
			response.setSubType(loginResult.getSubType());

			// dmscode
			response.setDmsCode(loginResult.getDmsCod());
			//设置登录Id
			response.setLoginId(loginId);
			// 返回结果
			return response;
		}
	}

	/**
	 * 检查客户端版本信息
	 * @param clientInfo 上传的客户端信息
	 * @param loginResult erp登录结果
	 * @return
	 */
    private JdResult<String> checkClientInfo(ClientInfo clientInfo,PdaStaff loginResult) {
    	JdResult<String> checkResult = new JdResult<String>();
    	checkResult.toSuccess();
    	//1、查询客户端登录验证配置信息
    	SysConfig checkConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_LOGIN_CHECK);
    	if(checkConfig!=null && StringHelper.isNotEmpty(checkConfig.getConfigContent())){
    		LoginCheckConfig loginCheckConfig = 
    				JsonHelper.fromJson(checkConfig.getConfigContent(), LoginCheckConfig.class);
    		boolean needCheck = loginCheckConfig.getMasterSwitch();
    		//2、校验总开关开启或者programTypes里包含登录客户端所属类型则进行校验
    		if(!needCheck
    				&& loginCheckConfig.getProgramTypes() != null
    				&& clientInfo.getProgramType() != null
    				&& loginCheckConfig.getProgramTypes().contains(clientInfo.getProgramType())){
    			needCheck = true;
    		}
    		//3、机构列表是否包含登录人所属机构则进行校验
    		if(!needCheck
    				&& loginCheckConfig.getOrgCodes() != null
    				&& loginResult != null
    				&& loginResult.getOrganizationId() != null
    				&& loginCheckConfig.getOrgCodes().contains(loginResult.getOrganizationId())){
    			needCheck = true;
    		}
    		//4、站点列表是否包含登录人所属站点则进行校验
    		if(!needCheck
    				&& loginCheckConfig.getSiteCodes() != null
    				&& loginResult != null
    				&& loginResult.getSiteId() != null
    				&& loginCheckConfig.getSiteCodes().contains(loginResult.getSiteId())){
    			needCheck = true;
    		}
    		/**
    		 * 4、版本校验：
    		 * 未上传版本类型WH_MINGYANG，验证失败
    		 */
    		if(needCheck){
    			if(StringHelper.isEmpty(clientInfo.getVersionName())){
    				checkResult.toFail("应用版本过低，请联系运维重新安装！");
    			}else{
    				List<ClientConfig> clientConfigs = clientConfigService.getBySiteCode(clientInfo.getVersionName());
    				if(clientConfigs == null || clientConfigs.isEmpty()){
    					checkResult.toFail("应用版本无效，请联系运维重新安装！");
    				}else{
    					boolean versionIsMatch = false;
    					String versionOnline = "";
    					for(ClientConfig clientConfig : clientConfigs){
    						if(clientConfig.getProgramType().equals(clientInfo.getProgramType())){
    							versionOnline = clientConfig.getVersionCode();
    						}
    						if(clientConfig.getVersionCode().equals(clientInfo.getVersionCode())){
    							versionIsMatch = true;
    							break;
    						}
    					}
    					if(!versionIsMatch){
    						if(ANDROID_LOGIN_PROGRAM_TYPE.equals(clientInfo.getProgramType()) || JSF_LOGIN_PROGRAM_TYPE.equals(clientInfo.getProgramType())){
								//安卓升级提示语
    							checkResult.toFail("版本过低！线上版本【"+versionOnline+"】，请点击左上角【检查新版本】按钮升级！");
							}else{
								//非安卓升级提示语
								checkResult.toFail("线上版本【"+versionOnline+"】，请退出重新登录/联系运维重新安装！");
							}
    					}
    				}
    			}
    		}
    	}
		return checkResult;
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
    public boolean checkClientTime(Date clientTime, Date serverTime){
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
		if(StringHelper.isNotEmpty(runningMode)){
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
}
