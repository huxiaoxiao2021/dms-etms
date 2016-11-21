package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.service.NewDeptWebService;
import com.jd.bluedragon.distribution.base.service.UserVerifyService;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.ssa.domain.UserInfo;
import com.jd.ssa.service.SsoService;


import com.jd.user.sdk.export.UserInfoExportService;
import com.jd.user.sdk.export.domain.LoginResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.MediaType;
import java.net.InetAddress;

/**
 * @author dudong
 * @date 2016/2/18
 */
public class UserVerifyServiceImpl implements UserVerifyService {

    private static final Log logger = LogFactory.getLog(UserVerifyServiceImpl.class);

//    private String passportUrl;
//
//    private String appPlatform;
//
//    private String token;

//    private DeptWebService deptWebService;
    @Autowired
    private NewDeptWebService newDeptWebService;

    @Autowired
    private UserInfoExportService userInfoRpc;

    @Override
    public UserInfo baseVerify(String name, String password) {
        try {
        	UserInfo userInfo = newDeptWebService.verify(name, password);
            return userInfo;
        } catch (Exception ex) {
            logger.error("deptWebService verify error", ex);
            return null;
        }
    }

    @Override
    public Boolean passportVerify(String pin, String password) {
        try {
            String md5Pwd = DigestUtils.md5Hex(password);
            String remoteIp = InetAddress.getLocalHost().getHostAddress();
            LoginResult loginResult = userInfoRpc.checkLoginForUnified(pin, md5Pwd, remoteIp);
            return LoginResult.PROCESS_CODE_SUCCESS == loginResult.getProcessCode();
        } catch (Exception ex) {
            logger.error("passportVerify verify error", ex);
            return Boolean.FALSE;
        }
    }

    //
//    @Override
//    public Boolean passportVerify(String pin, String password) {
//        try {
//            String remoteIp = InetAddress.getLocalHost().getHostAddress();
//            String pwd = Md5Helper.encode(password);
//            String param = "loginname=" + java.net.URLEncoder.encode(pin, "gbk") + "&loginpwd=" + pwd + "&remoteIp=" + remoteIp + "&appPlatform=" + appPlatform + "&token=" + token;
//
//            ClientRequest request = new ClientRequest(passportUrl);
//            request.accept(MediaType.WILDCARD);
//            request.body(javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED, param);
//            ClientResponse<String> response = request.post(String.class);
//
//            String result = response.getEntity();
//
//            if (result.contains("username") || result.contains("pwd")) {
//                return Boolean.FALSE;
//            } else {
//                return Boolean.TRUE;
//            }
//        } catch (Exception ex) {
//            logger.error("passportVerify verify error");
//            return Boolean.FALSE;
//        }
//    }

//    public String getPassportUrl() {
//        return passportUrl;
//    }
//
//    public void setPassportUrl(String passportUrl) {
//        this.passportUrl = passportUrl;
//    }
//
//    public String getAppPlatform() {
//        return appPlatform;
//    }
//
//    public void setAppPlatform(String appPlatform) {
//        this.appPlatform = appPlatform;
//    }
//
//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }

//    public DeptWebService getDeptWebService() {
//        return deptWebService;
//    }
//
//    public void setDeptWebService(DeptWebService deptWebService) {
//        this.deptWebService = deptWebService;
//    }

//	public NewDeptWebService getNewDeptWebService() {
//		return newDeptWebService;
//	}
//
//	public void setNewDeptWebService(NewDeptWebService newDeptWebService) {
//		this.newDeptWebService = newDeptWebService;
//	}
    
}
