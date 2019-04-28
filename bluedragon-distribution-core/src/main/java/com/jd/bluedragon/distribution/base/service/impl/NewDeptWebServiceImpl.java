package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.NewDeptWebService;
import com.jd.common.hrm.UimHelper;
import com.jd.ssa.domain.UserInfo;
import com.jd.ssa.service.SsoService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;

public class NewDeptWebServiceImpl implements NewDeptWebService{
	
	private static final Log logger = LogFactory.getLog(NewDeptWebServiceImpl.class);
	
	private SsoService ssoService;
	
	/**
	 * 校验用户密码 调用ssa服务的SsoService
	 * @param username 用户名
	 * @param password 密码
	 */
	public InvokeResult<UserInfo> verify(String username, String password) {
        InvokeResult<UserInfo> result = new InvokeResult<>();
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
		try {
			String pwd = UimHelper.md5(password);
			String remoteIp = InetAddress.getLocalHost().getHostAddress();
            UserInfo userInfo = ssoService.verify(username, pwd, remoteIp);
            result.setData(userInfo);
		}catch(Exception e){
            logger.error("SsoException verify error,认证失败");
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage(e.getMessage());
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
        InvokeResult<UserInfo> result = impl.verify("bjadmin","xinxibu456");
		System.out.println(result);
	}
}
