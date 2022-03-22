package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.distribution.api.request.AppUpgradeRequest;
import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.response.AppUpgradeResponse;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.LoginUserResponse;
import com.jd.bluedragon.distribution.client.domain.CheckMenuAuthRequest;
import com.jd.bluedragon.distribution.client.domain.CheckMenuAuthResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientHeartbeatRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientHeartbeatResponse;

/**
 * 
 * @ClassName: UserService
 * @Description: 用户操作相关的服务接口
 * @author: wuyoude
 * @date: 2018年12月27日 下午3:12:37
 *
 */
public interface UserService {
	/**
	 * 分拣客户端登录服务
	 * @param request
	 * @return
	 */
	public BaseResponse dmsClientLogin(LoginRequest request);
	/**
	 * 通过旧的jsf调用登录服务
	 * @param request
	 * @return
	 */
	public BaseResponse oldJsfLogin(LoginRequest request);
	/**
	 * 通过jsf调用登录服务
	 * @param request
	 * @return
	 */
	public LoginUserResponse jsfLogin(LoginRequest request);

	/**
	 * 通过jsf调用登录服务
	 * @param request
	 * @return
	 */
	LoginUserResponse jsfLoginWithToken(LoginRequest request);

	/**
	 * 客户端登录token验证
	 *
	 * @return
	 * @author fanggang7
	 * @time 2021-03-09 19:32:02 周二
	 */
	JdResult verifyClientLoginToken(String userErp, String deviceId, String token);

	/**
	 * 通过jsf调用,获取当前登录账户信息
	 * @param request
	 * @return
	 */
	public JdResult<LoginUserResponse> getLoginUser(LoginRequest request);

	/**
	 * 发送心跳信息
	 * @param dmsClientHeartbeatRequest
	 * @return
	 */
	JdResult<DmsClientHeartbeatResponse> sendHeartbeat(DmsClientHeartbeatRequest dmsClientHeartbeatRequest);
	/**
	 * 获取当前服务运行环境
	 * @return
	 */
	String getServerRunningMode();
	/**
	 * 校验菜单权限
	 * @param checkMenuAuthRequest
	 * @return
	 */
	JdResult<CheckMenuAuthResponse> checkMenuAuth(CheckMenuAuthRequest checkMenuAuthRequest);

    /**
     * APP升级校验
     * @param request
     * @return
     */
    JdResult<AppUpgradeResponse> checkAppVersion(AppUpgradeRequest request);
}
