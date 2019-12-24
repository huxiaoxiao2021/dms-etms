package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.NewDeptWebService;
import com.jd.common.hrm.UimHelper;
import com.jd.ssa.domain.UserInfo;
import com.jd.ssa.exception.SsoException;
import com.jd.ssa.service.SsoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NewDeptWebServiceImpl implements NewDeptWebService{
	
	private static final Logger log = LoggerFactory.getLogger(NewDeptWebServiceImpl.class);
	
	private SsoService ssoService;
	
	/**
	 * 校验用户密码 调用ssa服务的SsoService
	 * @param username 用户名
	 * @param password 密码
	 * @param loginVersion 客户端登录接口的版本
	 */
	public InvokeResult<UserInfo> verify(String username, String password, Byte loginVersion) {
        InvokeResult<UserInfo> result = new InvokeResult<>();
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
		try {
			String pwd = UimHelper.md5(password);
			if (null != loginVersion && loginVersion == 1) {
				pwd = password;
			}
			String remoteIp = InetAddress.getLocalHost().getHostAddress();
            UserInfo userInfo = ssoService.verify(username, pwd, remoteIp);
            result.setData(userInfo);
		}catch(SsoException e){
            log.error("SsoException verify error,认证失败",e);
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage(e.getMessage());
		}catch (UnknownHostException e) {
			log.error("获取本地ip异常",e);
			result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
			result.setMessage("验证失败!");
		}

		return result;
		
	}
	
	public SsoService getSsoService() {
		return ssoService;
	}

	public void setSsoService(SsoService ssoService) {
		this.ssoService = ssoService;
	}

	public static void main(String args[]){
		NewDeptWebServiceImpl impl = new NewDeptWebServiceImpl();
        InvokeResult<UserInfo> result = impl.verify("bjadmin","xinxibu456", (byte)1);
		System.out.println(result);
	}
}
