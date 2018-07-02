package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;

import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/5/9.
 */
public interface DmsBaseService {

    /**
     * 登录验证
     *
     * @param request
     * @return
     */
    BaseResponse login(LoginRequest request);

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
