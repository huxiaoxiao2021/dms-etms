package com.jd.bluedragon.distribution.base.service;


import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.ssa.domain.UserInfo;

/**
 * @author dudong
 * @date 2016/2/18
 */
public interface UserVerifyService {
    UserInfo baseVerify(String name, String password);
    Boolean passportVerify(String pin, String password, ClientInfo clientInfo);
}
