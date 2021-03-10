package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.LoginUserResponse;
import com.jd.bluedragon.distribution.command.JdResult;

import java.util.List;

/**
 * 发往物流网关的接口不要在此类中加方法
 * <p>
 * Created by lixin39 on 2018/5/9.
 */
public interface DmsBaseService {

    /**
     * 旧登录验证接口(安卓PDA)
     *
     * @param request
     * @return
     */
    BaseResponse login(LoginRequest request);
    /**
     * 客户端登录获取登录信息接口(安卓PDA)
     *
     * @param request
     * @return
     */
    LoginUserResponse clientLogin(LoginRequest request);

    /**
     * 客户端登录获取登录信息接口(安卓PDA)，增加erp认证
     *
     * @param request
     * @return
     * @author fanggang7
     * @time 2021-03-09 19:32:02 周二
     */
    LoginUserResponse clientLoginNew(LoginRequest request);
    /**
     * web页面，cookies自动登录后，调用该接口获取用户登录信息
     * @param request
     * @return
     */
    JdResult<LoginUserResponse> getLoginUser(LoginRequest request);
    /**
     * 获取始发网点详细信息
     *
     * @param code
     * @return
     */
    BaseResponse getSite(String code);

    /**
     * 获取服务器时间,由于物流网关不支持无参方法，故通过该方法跳转
     *
     * @param arg 任意值
     * @return
     */
    BaseResponse getServerDate(String arg);

    /**
     * 获取所有错误信息列表,由于物流网关不支持无参方法，故通过该方法跳转
     *
     * @param arg 任意值
     * @return
     */
    List<BaseResponse> getErrorList(String arg);

    /**
     * 登录获取RunTime信息,由于物流网关不支持无参方法，故通过该方法跳转
     *
     * @param arg 任意值
     * @return
     */
    List<BaseResponse> getRunNumber(String arg);

}
