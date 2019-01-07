package com.jd.bluedragon.distribution.base.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
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
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

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
	private static final Log logger = LogFactory.getLog(UserServiceImpl.class);
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
	 *  默认版本号-jsf登录接口
	 */
	private static final String JSF_LOGIN_DEFAULT_VERSION_CODE = "20180101JSF";
	/**
	 *  默认版本名称-jsf登录接口
	 */
	private static final String JSF_LOGIN_DEFAULT_VERSION_NAME = "Jsf_Login";
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private SysConfigService sysConfigService;
	@Autowired
	private SysLoginLogService sysLoginLogService;
	@Autowired
	private ClientConfigService clientConfigService;
	/**
	 * 分拣客户端登录服务
	 * @param request
	 * @return
	 */
    @JProfiler(jKey = "DMS.BASE.UserServiceImpl.dmsClientLogin",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public BaseResponse dmsClientLogin(LoginRequest request){
		return this.login(request, LOGIN_TYPE_DMS_CLIENT);
	}
	/**
	 * 通过jsf调用登录服务
	 * @param request
	 * @return
	 */
    @JProfiler(jKey = "DMS.BASE.UserServiceImpl.jsfLogin",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public BaseResponse jsfLogin(LoginRequest request){
		return this.login(request, LOGIN_TYPE_JSF);
	}
	/**
	 * 分拣客户端登录入口
	 * @param request 登录请求
	 * @param loginType 登录方式
	 * @return
	 */
	private BaseResponse login(LoginRequest request,Integer loginType) {
		logger.info("erpAccount is " + request.getErpAccount());

		String erpAccount = request.getErpAccount();
		String erpAccountPwd = request.getPassword();
		ClientInfo clientInfo = null;
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
		PdaStaff loginResult = baseService.login(erpAccount, erpAccountPwd,clientInfo);

		// 处理返回结果
		if (loginResult.isError()) {
			// 异常处理-验证失败，返回错误信息
			logger.info("erpAccount is " + erpAccount + " 验证失败，错误信息[" + loginResult.getErrormsg()
			        + "]");
			// 结果设置
			BaseResponse response = new BaseResponse(JdResponse.CODE_INTERNAL_ERROR,
			        loginResult.getErrormsg());
			// ERP账号
			response.setErpAccount(erpAccount);
			// ERP密码
			response.setPassword(erpAccountPwd);
			// 返回结果
			return response;
		} else {
			// 验证完成，返回相关信息
			logger.info("erpAccount is " + erpAccount + " 验证成功");
			try{
				//检查客户端版本信息，版本不一致，不允许登录
	            JdResult<String> checkResult = checkClientInfo(clientInfo,loginResult);
	            if(!checkResult.isSucceed()){
	            	clientInfo.setMatchFlag(SysLoginLog.MATCHFLAG_LOGIN_FAIL);
	            	sysLoginLogService.insert(loginResult, clientInfo);
	            	logger.warn("login-fail:params="+JsonHelper.toJson(request)+",msg="+checkResult.getMessage());
					BaseResponse response = new BaseResponse(JdResponse.CODE_INTERNAL_ERROR,
							checkResult.getMessage());
					// ERP账号
					response.setErpAccount(erpAccount);
					// ERP密码
					response.setPassword(erpAccountPwd);
					// 返回结果
					return response;
				}else{
					sysLoginLogService.insert(loginResult, clientInfo);
				}
	        }catch (Exception e){
	            logger.error("用户登录保存日志失败：" + erpAccount, e);
	        }
			if (null == loginResult.getSiteId()) {
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
    						checkResult.toFail("线上版本【"+versionOnline+"】，请退出重新登录/联系运维重新安装！");
    					}
    				}
    			}
    		}
    	}
		return checkResult;
	}
}
