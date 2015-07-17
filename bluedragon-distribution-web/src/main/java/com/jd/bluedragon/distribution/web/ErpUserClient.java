package com.jd.bluedragon.distribution.web;

import com.jd.common.web.LoginContext;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-4-18 上午11:38:07
 *
 * 当前登录用户信息获取
 */
public class ErpUserClient {

	/**
	 * 获取当前登录用户信息
	 * @return
	 */
	public static ErpUser getCurrUser() {
		LoginContext loginContext = LoginContext.getLoginContext();
		if (loginContext == null) {
			return null;
		}
		ErpUser erpUser = new ErpUser();
		erpUser.setUserId((int)loginContext.getUserId());
		erpUser.setUserCode(loginContext.getPin());
		erpUser.setUserName(loginContext.getNick());
		return erpUser;
	}
	
	/**
	 * @author zhaohc
	 * 当前登录用户类
	 */
	public static class ErpUser {
		/**
		 * 当前登录用户ID
		 */
		private Integer userId;
		/**
		 * 当前登录用户ERP账号
		 */
		private String userCode;
		/**
		 * 当前登录用户名称
		 */
		private String userName;

		public Integer getUserId() {
			return userId;
		}

		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		public String getUserCode() {
			return userCode;
		}

		public void setUserCode(String userCode) {
			this.userCode = userCode;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}
	}
}
