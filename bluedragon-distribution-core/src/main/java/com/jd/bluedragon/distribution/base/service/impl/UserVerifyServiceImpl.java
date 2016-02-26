package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.service.UserVerifyService;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.common.struts.interceptor.ws.DeptWebService;
import com.jd.common.struts.interceptor.ws.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import javax.ws.rs.core.MediaType;
import java.net.InetAddress;

/**
 * @author dudong
 * @date 2016/2/18
 */
public class UserVerifyServiceImpl implements UserVerifyService {

    private static final Log logger = LogFactory.getLog(UserVerifyServiceImpl.class);

    private String passportUrl;

    private String appPlatform;

    private String token;

    private DeptWebService deptWebService;

    @Override
    public User baseVerify(String name, String password) {
        try {
            return deptWebService.verify(name, password);
        } catch (Exception ex) {
            logger.error("deptWebService verify error");
            return null;
        }
    }

    @Override
    public Boolean passportVerify(String pin, String password) {
        try {
            String remoteIp = InetAddress.getLocalHost().getHostAddress();
            String pwd = Md5Helper.encode(password);
            String param = "loginname=" + java.net.URLEncoder.encode(pin, "gbk") + "&loginpwd=" + pwd + "&remoteIp=" + remoteIp + "&appPlatform=" + appPlatform + "&token=" + token;

            ClientRequest request = new ClientRequest(passportUrl);
            request.accept(MediaType.WILDCARD);
            request.body(javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE, param);
            ClientResponse<String> response = request.post(String.class);

            String result = response.getEntity();

            if (result.contains("username") || result.contains("pwd")) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        } catch (Exception ex) {
            logger.error("passportVerify verify error");
            return Boolean.FALSE;
        }
    }


    public String getPassportUrl() {
        return passportUrl;
    }

    public void setPassportUrl(String passportUrl) {
        this.passportUrl = passportUrl;
    }

    public String getAppPlatform() {
        return appPlatform;
    }

    public void setAppPlatform(String appPlatform) {
        this.appPlatform = appPlatform;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DeptWebService getDeptWebService() {
        return deptWebService;
    }

    public void setDeptWebService(DeptWebService deptWebService) {
        this.deptWebService = deptWebService;
    }
}
