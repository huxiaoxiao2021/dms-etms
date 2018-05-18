package com.jd.bluedragon.distribution.base.service.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.distribution.base.service.NewDeptWebService;
import com.jd.common.hrm.UimHelper;
import com.jd.ssa.domain.UserInfo;
import com.jd.ssa.exception.SsoException;
import com.jd.ssa.service.SsoService;
import com.jd.ssa.utils.SSOHelper;
import javax.servlet.http.HttpServletRequest;

public class NewDeptWebServiceImpl implements NewDeptWebService{
	
	private static final Log logger = LogFactory.getLog(NewDeptWebServiceImpl.class);
	
	private SsoService ssoService;
	
	/**
	 * 校验用户密码 调用ssa服务的SsoService
	 * @param username 用户名
	 * @param password 密码
	 */
	public UserInfo verify(String username, String password) {
		UserInfo userInfo = null;
		try {
			String pwd = UimHelper.md5(password);
			String remoteIp = InetAddress.getLocalHost().getHostAddress();
			userInfo = ssoService.verify(username, pwd, remoteIp);
		}catch(Exception e){
			logger.error("SsoException verify error,认证失败");
		}
		
		return userInfo;
		
	}
	
	public SsoService getSsoService() {
		return ssoService;
	}

	public void setSsoService(SsoService ssoService) {
		this.ssoService = ssoService;
	}

	public static void main(String args[]){
		NewDeptWebServiceImpl impl = new NewDeptWebServiceImpl();
		UserInfo userInfo = impl.verify("bjadmin","xinxibu456");
		System.out.println(userInfo);
	}
}
