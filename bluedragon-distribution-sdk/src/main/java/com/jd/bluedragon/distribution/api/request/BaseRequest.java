package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class BaseRequest  extends JdRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5856898273901032740L;
	
	private String erpAccount;
	
	private String password;

	public String getErpAccount() {
		return erpAccount;
	}

	public void setErpAccount(String erpAccount) {
		this.erpAccount = erpAccount;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
