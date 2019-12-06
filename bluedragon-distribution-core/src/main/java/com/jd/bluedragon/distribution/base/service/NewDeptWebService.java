package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.ssa.domain.UserInfo;

public interface NewDeptWebService {
	
	public InvokeResult<UserInfo> verify(String username, String password, Byte loginVersion);

	
}
