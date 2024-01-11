package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.response.LoginUserResponse;

import java.io.Serializable;

/**
 * 登录信息jmq
 */

public class LoginInfoDto implements Serializable {
    //登录请求参数
    private LoginRequest loginRequest;
    //登录返回信息
    private LoginUserResponse loginUserResponse;

    public LoginRequest getLoginRequest() {
        return loginRequest;
    }

    public void setLoginRequest(LoginRequest loginRequest) {
        this.loginRequest = loginRequest;
    }

    public LoginUserResponse getLoginUserResponse() {
        return loginUserResponse;
    }

    public void setLoginUserResponse(LoginUserResponse loginUserResponse) {
        this.loginUserResponse = loginUserResponse;
    }
}
