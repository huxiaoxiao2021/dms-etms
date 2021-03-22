package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.LoginUserResponse;
import com.jd.bluedragon.distribution.base.service.UserService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.external.service.DmsBaseService;
import com.jd.bluedragon.distribution.rest.base.BaseResource;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsBaseService")
public class DmsBaseServiceImpl implements DmsBaseService {

    @Autowired
    @Qualifier("baseResource")
    private BaseResource baseResource;

	@Autowired
	private UserService userService;

	@Autowired
    UccPropertyConfiguration uccPropertyConfiguration;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBaseServiceImpl.login", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BaseResponse login(LoginRequest request) {
        if (uccPropertyConfiguration.isDisablePdaOldLogin()) {
            //登陆已经切换到新clientlogin,此接口关闭,提示强制升级
            BaseResponse response = new BaseResponse();
            response.setErpAccount(request.getErpAccount());
            response.setPassword(request.getPassword());

            response.setCode(301);
            response.setMessage("此版本pda已停用,请点击左上角检查更新版本");
            return response;
        }

        return userService.oldJsfLogin(request);
    }
    @Override
    @JProfiler(jKey = "DMSWEB.DmsBaseServiceImpl.clientLogin", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public LoginUserResponse clientLogin(LoginRequest request) {
        return userService.jsfLogin(request);
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
    @JProfiler(jKey = "DMSWEB.DmsBaseServiceImpl.clientLoginNew", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public LoginUserResponse clientLoginNew(LoginRequest request) {
        return userService.jsfLoginWithToken(request);
    }

    /**
     * 客户端登录token验证
     *
     * @return
     * @author fanggang7
     * @time 2021-03-09 19:32:02 周二
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DmsBaseServiceImpl.verifyClientLoginToken", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdResult verifyClientLoginToken(String deviceId, String token) {
        return userService.verifyClientLoginToken(deviceId, token);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBaseServiceImpl.getSite", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BaseResponse getSite(String code) {
        if (null==code){
            BaseResponse response = new BaseResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }
        return baseResource.getSite(code);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBaseServiceImpl.getServerDate", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BaseResponse getServerDate(String arg) {
        return baseResource.getServerDate();
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBaseServiceImpl.getErrorList", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public List<BaseResponse> getErrorList(String arg) {
        return baseResource.getErrorList();
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBaseServiceImpl.getRunNumber", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public List<BaseResponse> getRunNumber(String arg) {
        return baseResource.getRunNumber();
    }

	@Override
    @JProfiler(jKey = "DMSWEB.DmsBaseServiceImpl.getLoginUser", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public JdResult<LoginUserResponse> getLoginUser(LoginRequest request) {
		return userService.getLoginUser(request);
	}
}
