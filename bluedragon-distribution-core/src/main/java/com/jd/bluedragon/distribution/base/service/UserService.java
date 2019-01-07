package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;

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
	 * 通过jsf调用登录服务
	 * @param request
	 * @return
	 */
	public BaseResponse jsfLogin(LoginRequest request);
}
