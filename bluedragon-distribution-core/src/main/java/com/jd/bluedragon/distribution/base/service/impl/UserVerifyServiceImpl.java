package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.service.NewDeptWebService;
import com.jd.bluedragon.distribution.base.service.UserVerifyService;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.ssa.domain.UserInfo;
import com.jd.user.sdk.export.UserPassportExportService;
import com.jd.user.sdk.export.constant.Constants;
import com.jd.user.sdk.export.domain.passport.LoginResult;
import com.jd.user.sdk.export.domain.passport.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dudong
 * @date 2016/2/18
 */
public class UserVerifyServiceImpl implements UserVerifyService {

    private static final Log logger = LogFactory.getLog(UserVerifyServiceImpl.class);

    private static final String NONE = "NONE";
    private static final String SOURCE = "ql_dms";

//    private String passportUrl;
//
//    private String appPlatform;
//
//    private String token;

    //    private DeptWebService deptWebService;
    @Autowired
    private NewDeptWebService newDeptWebService;

    @Autowired
    private UserPassportExportService userInfoRpc;

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
    public Boolean passportVerify(String pin, String password, ClientInfo clientInfo) {
        try {
            String md5Pwd = DigestUtils.md5Hex(password);
            String remoteIp = InetAddress.getLocalHost().getHostAddress();
            LoginParam loginParam = new LoginParam();
            loginParam.setSource(SOURCE);
            loginParam.setAuthType(1);
            loginParam.setLoginName(pin);
            loginParam.setPassword(md5Pwd);
            loginParam.setUserIp(remoteIp);
            loginParam.setDeviceName(NONE);
            loginParam.setDeviceOSVersion(NONE);
            loginParam.setDeviceOS(NONE);
            loginParam.setDeviceVersion(NONE);
            Map<String, String> extInfo = new HashMap(20);
            extInfo.put(Constants.LoginParam.APP_ID, NONE);
            extInfo.put(Constants.LoginParam.EQUIPMNET_ID, NONE);
            extInfo.put(Constants.LoginParam.OPEN_UDID, NONE);
            extInfo.put(Constants.LoginParam.UUID, NONE);
            if (clientInfo.getVersionCode().contains("D") || clientInfo.getVersionCode().contains("F") || clientInfo.getVersionCode().contains("R")) {
                extInfo.put(Constants.LoginParam.CHANNEL, "10");
            } else if (clientInfo.getVersionCode().contains("WP") || clientInfo.getVersionCode().contains("WM")) {
                extInfo.put(Constants.LoginParam.CHANNEL, "2");
            } else {
                extInfo.put(Constants.LoginParam.CHANNEL, NONE);
            }
            loginParam.addAllExtInfo(extInfo);
            LoginResult loginResult = userInfoRpc.login(loginParam);
            return loginResult.isSuccess();
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
