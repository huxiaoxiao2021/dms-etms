package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.request.LoginWithTokenVerifyRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.LoginUserResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.ClientRunningModeConfig;
import com.jd.bluedragon.distribution.base.domain.ClientRunningModeConfig.ClientRunningModeConfigItem;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.*;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.bluedragon.sdk.modules.client.DmsClientMessages;
import com.jd.bluedragon.sdk.modules.client.LoginStatusEnum;
import com.jd.bluedragon.sdk.modules.client.LogoutTypeEnum;
import com.jd.bluedragon.sdk.modules.client.ProgramTypeEnum;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientHeartbeatRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientHeartbeatResponse;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
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

import java.util.Date;
import java.util.Objects;
import java.util.UUID;
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
	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	@Qualifier("baseService")
	protected LoginService baseService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

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
		return baseService.clientLoginIn(request);
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
        LoginUserResponse loginUserResponse = baseService.clientLoginIn(request);
        this.getAndSaveToken(request, loginUserResponse);
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
            String clientLoginDeviceIdKey = String.format(CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_LOGIN_USER_DEVICE_ID, request.getUserCode(), deviceId);
            jimdbCacheService.setEx(clientLoginDeviceIdKey, token, CacheKeyConstants.CACHE_KEY_FORMAT_CLIENT_LOGIN_DEVICE_ID_EXPIRE_TIME, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("UserServiceImpl.getAndSaveToken exception ", e);
            loginUserResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            loginUserResponse.setMessage("保存登录token数据异常");
            return false;
        }
        return true;
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
}
