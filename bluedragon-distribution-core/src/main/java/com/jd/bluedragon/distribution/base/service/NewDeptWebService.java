package com.jd.bluedragon.distribution.base.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import com.jd.ssa.domain.UserInfo;

import net.sf.json.JSONObject;

public interface NewDeptWebService {
	
	public UserInfo verify(String username,String password);

	
}
