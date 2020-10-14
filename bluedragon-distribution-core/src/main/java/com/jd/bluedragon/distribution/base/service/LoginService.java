package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.response.LoginUserResponse;

public interface LoginService {

    /**
     * 新登录接口，同时返回用户的站点和分拣中心
     *
     * @param request
     * @return
     */
    LoginUserResponse clientLoginIn(LoginRequest request);
}
