package com.jd.bluedragon.external.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.external.service.EmsServiceManager;
import com.jd.postal.GetPrintDatasPortType;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

@Service
public class EmsServiceManagerImpl implements EmsServiceManager{

    @Autowired
    private GetPrintDatasPortType getPrintDatasPortType;
    
	@JProfiler(jKey = "DmsWeb.com.jd.postal.GetPrintDatasPortType.printEMSDatas",
			jAppName = Constants.UMP_APP_NAME_DMSWEB,
			mState = {JProEnum.TP,JProEnum.FunctionError})
	@Override
	public String printEMSDatas(String xmlStr) {
		return getPrintDatasPortType.printEMSDatas(xmlStr);
	}
	
}
