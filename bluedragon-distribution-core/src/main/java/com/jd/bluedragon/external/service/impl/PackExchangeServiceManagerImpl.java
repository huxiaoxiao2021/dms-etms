package com.jd.bluedragon.external.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.external.service.PackExchangeServiceManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.wms.packExchange.BaseWebService;
import com.jd.wms.packExchange.Result;

@Service
public class PackExchangeServiceManagerImpl implements PackExchangeServiceManager{

	@Autowired
	BaseWebService baseWebService;
    
	@JProfiler(jKey = "DmsWeb.com.jd.wms.packExchange.BaseWebService.queryWs",
			jAppName = Constants.UMP_APP_NAME_DMSWEB,
			mState = {JProEnum.TP,JProEnum.FunctionError})
	@Override
	public Result queryWs(String arg0, String arg1) {
		return baseWebService.queryWs(arg0, arg1);
	}
	
}
