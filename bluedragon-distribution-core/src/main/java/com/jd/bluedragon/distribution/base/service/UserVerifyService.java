package com.jd.bluedragon.distribution.base.service;


import com.jd.common.struts.interceptor.ws.User;

/**
 * @author dudong
 * @date 2016/2/18
 */
public interface UserVerifyService {
    User baseVerify(String name, String password);
    Boolean passportVerify(String pin, String password);
}
