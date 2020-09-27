package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.domain.DmsClientConfigInfo;
import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.response.LoginUserResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.LoginCheckConfig;
import com.jd.bluedragon.distribution.base.domain.PdaStaff;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.impl.UserServiceImpl;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.bluedragon.distribution.sysloginlog.domain.SysLoginLog;
import com.jd.bluedragon.distribution.sysloginlog.service.SysLoginLogService;
import com.jd.bluedragon.distribution.version.domain.ClientConfig;
import com.jd.bluedragon.distribution.version.service.ClientConfigService;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLoginRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLoginResponse;
import com.jd.bluedragon.service.remote.client.DmsClientManager;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Objects;

public abstract class AbstractBaseUserService implements LoginService {

    private static final Logger log = LoggerFactory.getLogger(AbstractBaseUserService.class);

    private static final String RUNNING_MODE_UAT = "uat";

    /**
     *	登录方式-分拣客户端（PDA、打印、标签设计器）
     */
    protected static final Integer LOGIN_TYPE_DMS_CLIENT = 1;
    /**
     *  登录方式-jsf（安卓版PDA）
     */
    protected static final Integer LOGIN_TYPE_JSF = 2;
    /**
     *  程序类型-jsf登录接口
     */
    protected static final Integer JSF_LOGIN_PROGRAM_TYPE = 50;

    /**
     *  程序类型-安卓登录接口
     */
    protected static final Integer ANDROID_LOGIN_PROGRAM_TYPE = 60;
    /**
     *  默认基础底包版本号-jsf登录接口
     */
    protected static final String JSF_LOGIN_DEFAULT_BASE_VERSION_CODE = "60-20200925";
    /**
     *  默认版本号-jsf登录接口
     */
    protected static final String JSF_LOGIN_DEFAULT_VERSION_CODE = "20180101JSF";
    /**
     *  默认版本名称-jsf登录接口
     */
    protected static final String JSF_LOGIN_DEFAULT_VERSION_NAME = "Jsf_Login";

    /**
     * 当前应用的环境（prod-全国 pre-华中 uat-UAT test-测试）
     */
    @Value("${app.config.runningMode:prod}")
    protected String runningMode;
    @Autowired
    protected DmsClientManager dmsClientManager;
    @Autowired
    protected SysLoginLogService sysLoginLogService;
    @Autowired
    protected SysConfigService sysConfigService;
    @Autowired
    protected ClientConfigService clientConfigService;
    @Autowired
    @Qualifier("basicPrimaryWS")
    private BasicPrimaryWS basicPrimaryWS;

    @Override
    @JProfiler(jKey = "DMS.BASE.AbstractBaseUserService.clientLoginIn", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public LoginUserResponse clientLoginIn(LoginRequest request) {
        LoginUserResponse response = new LoginUserResponse();
        if(request == null){
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }
        request.setLoginVersion((byte)1);
        response = this.login(request, LOGIN_TYPE_DMS_CLIENT);
        if (response.getCode().equals(JdResponse.CODE_OK)) {
            this.bindSite2LoginUser(response);
        }
        ClientInfo clientInfo = JsonHelper.fromJson(request.getClientInfo(), ClientInfo.class);
        String sysconfRunningMode = response.getDmsClientConfigInfo()!= null?response.getDmsClientConfigInfo().getRunningMode():"";
        if(isLoginFormAndroidAndCurrentIsUat(clientInfo,sysconfRunningMode)){
            response.setCode(JdResponse.CODE_WRONG_STATUS);
            String msg = String.format("当前登录账号[%s]不支持[%s]登录,请尝试在登录首页右上角修改正式环境再登录！",request.getErpAccount(),runningMode);
            response.setMessage(msg);
            return response;
        }
        return response;
    }


    /**
     * 只控制 登录来源是安卓的；逻辑为：系统给当前账户配置uat权限并且当前环境是uat，才能登录
     * @param clientInfo
     * @param sysconfRunningMode
     * @return
     */
    private boolean isLoginFormAndroidAndCurrentIsUat(ClientInfo clientInfo, String sysconfRunningMode) {
        return clientInfo != null && Objects.equals(clientInfo.getProgramType(), UserServiceImpl.ANDROID_LOGIN_PROGRAM_TYPE)
                && runningMode.contains(RUNNING_MODE_UAT) && !Objects.equals(runningMode,sysconfRunningMode);
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
                if (null != dtoStaff.getDmsId() && !dtoStaff.getDmsId().equals(dtoStaff.getSiteCode()) && dtoStaff.getDmsId() > 0) {
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
    protected LoginUserResponse login(LoginRequest request, Integer loginType) {
        log.info("erpAccount is {}" , request.getErpAccount());

        String erpAccount = request.getErpAccount();
        String erpAccountPwd = request.getPassword();
        ClientInfo clientInfo = null;
        Long loginId = 0L;
        Boolean needUpdate = Boolean.FALSE;
        Boolean forceUpdate = Boolean.FALSE;
        DmsClientConfigInfo dmsClientConfigInfo = null;
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
        LoginClientService loginClient = selectLoginClient();
        loginClient.selectErpValidateService(erpAccount);
        PdaStaff loginResult = loginClient.login(erpAccount, erpAccountPwd, clientInfo, request.getLoginVersion());

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
                JdResult<String> checkResult = checkClientInfo(request,clientInfo,loginResult);
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
                    DmsClientLoginRequest dmsClientLoginRequest = generateDmsClientLoginRequest(request,loginResult, clientInfo);
                    //调用jsf登录记录接口
                    JdResult<DmsClientLoginResponse> loginResponse = dmsClientManager.login(dmsClientLoginRequest);
                    if(loginResponse != null
                            && loginResponse.isSucceed()
                            && loginResponse.getData() != null){
                        loginId = loginResponse.getData().getLoginId();
                        needUpdate = loginResponse.getData().getNeedUpdate();
                        forceUpdate = loginResponse.getData().getForceUpdate();
                        if(loginResponse.getData().getDmsClientConfigInfo() != null){
                            dmsClientConfigInfo = new DmsClientConfigInfo();
                            BeanUtils.copyProperties(dmsClientConfigInfo,loginResponse.getData().getDmsClientConfigInfo());
                        }
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
            response.setNeedUpdate(needUpdate);
            response.setForceUpdate(forceUpdate);
            response.setDmsClientConfigInfo(dmsClientConfigInfo);
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
    private JdResult<String> checkClientInfo(LoginRequest request,ClientInfo clientInfo,PdaStaff loginResult) {
        JdResult<String> checkResult = new JdResult<String>();
        checkResult.toSuccess();
        //1、查询客户端登录验证配置信息
        SysConfig checkConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_LOGIN_CHECK);
        if(checkConfig!=null && StringHelper.isNotEmpty(checkConfig.getConfigContent())){
            LoginCheckConfig loginCheckConfig =
                    JsonHelper.fromJson(checkConfig.getConfigContent(), LoginCheckConfig.class);
            //校验底包版本号
            if(StringHelper.isEmpty(request.getBaseVersionCode())
            		||(loginCheckConfig.getBaseVersionCodes() != null 
            			&&!loginCheckConfig.getBaseVersionCodes().contains(request.getBaseVersionCode()))){
            	checkResult.toFail("应用版本过低，请联系运维重新安装！");
            	return checkResult;
            }
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
            //客户端设置了不校验版本，改为false
            if(needCheck
            		&& Boolean.FALSE.equals(request.getCheckVersion())){
            	needCheck = false;
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

    /**
     * 生成客户端登录请求
     * @param loginResult
     * @param clientInfo
     * @return
     */
    private DmsClientLoginRequest generateDmsClientLoginRequest(LoginRequest loginRequest,PdaStaff loginResult,
                                                                ClientInfo clientInfo) {
        DmsClientLoginRequest dmsClientLoginRequest = new DmsClientLoginRequest();
        dmsClientLoginRequest.setProgramType(clientInfo.getProgramType());
        dmsClientLoginRequest.setVersionCode(clientInfo.getVersionCode());
        dmsClientLoginRequest.setSystemCode(Constants.SYS_CODE_DMS);
        dmsClientLoginRequest.setRunningMode(this.runningMode);
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
        dmsClientLoginRequest.setUserCode(loginRequest.getErpAccount());
        dmsClientLoginRequest.setUserName(loginResult.getStaffName());

        dmsClientLoginRequest.setMacAdress(clientInfo.getMacAdress());
        dmsClientLoginRequest.setMachineCode(clientInfo.getMachineName());
        dmsClientLoginRequest.setIpv4(clientInfo.getIpv4());
        dmsClientLoginRequest.setIpv6(clientInfo.getIpv6());
        return dmsClientLoginRequest;
    }

    /***
     * @description: 选择校验客户端
     * @return: com.jd.bluedragon.distribution.base.service.LoginClientService
     * @author: lql
     * @date: 2020/8/21 14:06
     **/
    protected abstract LoginClientService selectLoginClient();


}
