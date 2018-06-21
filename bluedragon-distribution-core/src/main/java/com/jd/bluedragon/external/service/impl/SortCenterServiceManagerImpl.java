package com.jd.bluedragon.external.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.external.service.SortCenterServiceManager;
import com.jd.pop.sortcenter.ws.SortCenterService;
import com.jd.pop.sortcenter.ws.VenderOperInfoResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
@Service
public class SortCenterServiceManagerImpl implements SortCenterServiceManager{
	@Autowired
	private SortCenterService sortCenterService;
	
	@JProfiler(jKey = "DmsWeb.com.jd.pop.sortcenter.ws.SortCenterService.searchInfoForByOrderId",
			jAppName = Constants.UMP_APP_NAME_DMSWEB,
			mState = {JProEnum.TP,JProEnum.FunctionError})
	@Override
	public VenderOperInfoResult searchInfoForByOrderId(long arg0) {
		return sortCenterService.searchInfoForByOrderId(arg0);
	}
}
