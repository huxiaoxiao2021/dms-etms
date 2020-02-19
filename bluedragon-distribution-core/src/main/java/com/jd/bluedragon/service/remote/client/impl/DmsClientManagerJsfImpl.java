package com.jd.bluedragon.service.remote.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.sdk.common.dto.ApiResult;
import com.jd.bluedragon.sdk.modules.client.DmsClientJsfService;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientHeartbeatRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientHeartbeatResponse;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLoginRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLoginResponse;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLogoutRequest;
import com.jd.bluedragon.service.remote.client.DmsClientManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

/**
 * 
 * @ClassName: DmsClientManagerJsfImpl
 * @Description: 客户端相关远程服务
 * @author: wuyoude
 * @date: 2020年1月2日 下午4:31:10
 *
 */
@Service
public class DmsClientManagerJsfImpl implements DmsClientManager{
	
	@Autowired
	private DmsClientJsfService dmsClientJsfService;
	/**
	 * 登录接口
	 * @param dmsClientLoginRequest
	 * @return
	 */
    @JProfiler(jKey = "dmsWeb.jsf.client.business.dmsClientJsfService.login",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<DmsClientLoginResponse> login(DmsClientLoginRequest dmsClientLoginRequest){
		return toJdResult(dmsClientJsfService.login(dmsClientLoginRequest));
	}
	/**
	 * 登出接口
	 * @param dmsClientLogoutRequest
	 * @return
	 */
    @JProfiler(jKey = "dmsWeb.jsf.client.business.dmsClientJsfService.logout",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<Boolean> logout(DmsClientLogoutRequest dmsClientLogoutRequest){
		return toJdResult(dmsClientJsfService.logout(dmsClientLogoutRequest));
	}
	/**
	 * 发送心跳请求
	 * @param dmsClientHeartbeatRequest
	 * @return
	 */
    @JProfiler(jKey = "dmsWeb.jsf.client.business.dmsClientJsfService.sendHeartbeat",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<DmsClientHeartbeatResponse> sendHeartbeat(DmsClientHeartbeatRequest dmsClientHeartbeatRequest){
		return toJdResult(dmsClientJsfService.sendHeartbeat(dmsClientHeartbeatRequest));
	}
	/**
	 * 结果集转换
	 * @param apiResult
	 * @return
	 */
	private <D> JdResult<D> toJdResult(ApiResult<D> apiResult){
		JdResult<D> jdResult = new JdResult<D>();
		if(apiResult != null){
			jdResult.setCode(apiResult.getCode());
			jdResult.setMessageCode(apiResult.getMessageCode());
			jdResult.setMessage(apiResult.getMessage());
			jdResult.setData(apiResult.getData());
		}else{
			jdResult.toFail("调用远程jsf服务失败！");
		}
		return jdResult;
	}
}