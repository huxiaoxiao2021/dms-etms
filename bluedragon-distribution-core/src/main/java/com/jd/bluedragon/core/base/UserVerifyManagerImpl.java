package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.base.domain.BasePdaUserDto;
import com.jd.bluedragon.distribution.base.service.NewDeptWebService;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.ssa.domain.UserInfo;
import com.jd.user.sdk.export.UserPassportExportService;
import com.jd.user.sdk.export.constant.Constants;
import com.jd.user.sdk.export.domain.passport.LoginParam;
import com.jd.user.sdk.export.domain.passport.LoginResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangwenshu
 * @Description: 类描述信息
 * @date 2018年10月19日 10时:33分
 */
@Service("userVerifyManager")
public class UserVerifyManagerImpl implements UserVerifyManager {
    private static final Log logger = LogFactory.getLog(UserVerifyManagerImpl.class);

    private static final String NONE = "NONE";
    private static final String SOURCE = "ql_dms";
    /**
     * 登录设备是手持PDA
     */
    private static final String HANDLE_PDA_D = "D";
    private static final String HANDLE_PDA_F = "F";
    private static final String HANDLE_PDA_R = "R";
    /**
     * 登录设备是客户端
     */
    private static final String PC_WM = "WM";
    private static final String PC_WP = "WP";

    //登录方式
    private static final Integer AUTHTYPE = 1;

    //登录渠道或终端
    //PC 端
    private static final String PC = "2";
    //手持PDA
    private static final String PDA = "10";

    @Autowired
    private NewDeptWebService newDeptWebService;

    @Autowired
    private UserPassportExportService userInfoRpc;

    /**
     * 自营验证
     *
     * @param name
     * @param password
     * @return
     */
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

    /**
     * 三方验证
     *
     * @param pin
     * @param password
     * @param clientInfo
     * @return
     */
    @Override
    public BasePdaUserDto passportVerify(String pin, String password, ClientInfo clientInfo) {
        BasePdaUserDto basePdaUserDto = new BasePdaUserDto();
        try {
            String md5Pwd = DigestUtils.md5Hex(password);
            String remoteIp = InetAddress.getLocalHost().getHostAddress();
            LoginParam loginParam = new LoginParam();
            loginParam.setSource(SOURCE);
            loginParam.setAuthType(AUTHTYPE);
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
            if (clientInfo == null || clientInfo.getVersionCode() == null) {
                extInfo.put(Constants.LoginParam.CHANNEL, PDA);
            } else {
                if (clientInfo.getVersionCode().contains(HANDLE_PDA_D) || clientInfo.getVersionCode().contains(HANDLE_PDA_F) || clientInfo.getVersionCode().contains(HANDLE_PDA_R)) {
                    extInfo.put(Constants.LoginParam.CHANNEL, PDA);
                } else if (clientInfo.getVersionCode().contains(PC_WM) || clientInfo.getVersionCode().contains(PC_WP)) {
                    extInfo.put(Constants.LoginParam.CHANNEL, PC);
                } else {
                    extInfo.put(Constants.LoginParam.CHANNEL, PDA);
                }
            }
            loginParam.addAllExtInfo(extInfo);
            LoginResult loginResult = userInfoRpc.login(loginParam);
            if (loginResult == null) {
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_JSF_FAILUE);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_JSF_FAILUE_MSG);
                return basePdaUserDto;
            }
            if(loginResult.getResultCode().equals(com.jd.bluedragon.Constants.PDA_USER_GETINFO_SUCCESS)){
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_GETINFO_SUCCESS);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_GETINFO_SUCCESS_MSG);
                logger.warn("3pl登录验证成功");
            } else if (loginResult.getResultCode().equals(com.jd.bluedragon.Constants.PDA_USER_NO_EXIT)) {
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_NO_EXIT);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_NO_EXIT_MSG);
            } else if (loginResult.getResultCode().equals(com.jd.bluedragon.Constants.PDA_USER_PASSWORD_WRONG)) {
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_PASSWORD_WRONG);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_PASS_WORD_WRONG_MSG);
            } else if (loginResult.getResultCode().equals(com.jd.bluedragon.Constants.PDA_USER_LOCKED)) {
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_LOCKED);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_LOCKED_MSG);
            } else if (loginResult.getResultCode().equals(com.jd.bluedragon.Constants.PDA_USER_BUSY)) {
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_BUSY);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_BUSY_MSG);
            } else if (loginResult.getResultCode().equals(com.jd.bluedragon.Constants.PDA_USER_NO_VERIFY)) {
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_NO_VERIFY);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_NO_VERIFY_MSG);
            } else if (loginResult.getResultCode().equals(com.jd.bluedragon.Constants.PDA_USER_SECURITY_LOCKED)) {
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_SECURITY_LOCKED);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_SECURITY_LOCKED_MSG);
            } else if (loginResult.getResultCode().equals(com.jd.bluedragon.Constants.PDA_USER_LOGOUT)) {
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_LOGOUT);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_LOGOUT_MSG);
            } else if (loginResult.getResultCode().equals(com.jd.bluedragon.Constants.PDA_USER_SECURITY_CHECK)) {
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_SECURITY_CHECK);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_SECURITY_CHECK_MSG);
            } else if (loginResult.getResultCode().equals(com.jd.bluedragon.Constants.PDA_USER_NO_USE)) {
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_NO_USE);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_NO_USE_MSG);
            } else if (loginResult.getResultCode().equals(com.jd.bluedragon.Constants.PDA_USER_IP_WRONG)) {
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_IP_WRONG);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_IP_WRONG_MSG);
            } else {
                basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_LOGIN_FAILUE);
                basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_LOGIN_FAILUE_MSG);
                logger.warn("3pl登录验证未知原因导致失败");
            }
            return basePdaUserDto;
        } catch (Exception ex) {
            logger.error("passportVerify verify error", ex);
            basePdaUserDto.setErrorCode(com.jd.bluedragon.Constants.PDA_USER_ABNORMAL);
            basePdaUserDto.setMessage(com.jd.bluedragon.Constants.PDA_USER_ABNORMAL_MSG);
            return basePdaUserDto;
        }
    }
}
