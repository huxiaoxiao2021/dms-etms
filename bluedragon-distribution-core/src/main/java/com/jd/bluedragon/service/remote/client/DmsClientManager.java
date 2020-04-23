package com.jd.bluedragon.service.remote.client;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientHeartbeatRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientHeartbeatResponse;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLoginRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLoginResponse;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLogoutRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientVersionRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientVersionResponse;

/**
 * 
 * @ClassName: DmsClientManager
 * @Description: 客户端相关远程服务
 * @author: wuyoude
 * @date: 2020年1月2日 下午4:31:10
 *
 */
public interface DmsClientManager {
	/**
	 * 登录接口
	 * @param dmsClientLoginRequest
	 * @return
	 */
	JdResult<DmsClientLoginResponse> login(DmsClientLoginRequest dmsClientLoginRequest);
	/**
	 * 登出接口
	 * @param dmsClientLogoutRequest
	 * @return
	 */
	JdResult<Boolean> logout(DmsClientLogoutRequest dmsClientLogoutRequest);
	/**
	 * 发送心跳请求
	 * @param dmsClientHeartbeatRequest
	 * @return
	 */
	JdResult<DmsClientHeartbeatResponse> sendHeartbeat(DmsClientHeartbeatRequest dmsClientHeartbeatRequest);
	/**
	 * 获取版本号信息
	 * @param dmsClientVersionRequest
	 * @return
	 */
	JdResult<DmsClientVersionResponse> getClientVersion(DmsClientVersionRequest dmsClientVersionRequest);
}
