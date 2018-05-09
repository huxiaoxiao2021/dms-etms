package com.jd.bluedragon.external.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.external.service.LossServiceManager;
import com.jd.loss.client.BlueDragonWebService;
import com.jd.loss.client.LossProduct;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

@Service
public class LossServiceManagerImpl implements LossServiceManager{
	
	@Autowired
	BlueDragonWebService blueDragonWebService;
	
	@JProfiler(jKey = "DmsWeb.com.jd.loss.client.BlueDragonWebService.getLossProductCountOrderId",
			jAppName = Constants.UMP_APP_NAME_DMSWEB,
			mState = {JProEnum.TP,JProEnum.FunctionError})
	@Override
	public int getLossProductCountOrderId(String orderId) {
		return blueDragonWebService.getLossProductCountOrderId(orderId);
	}
	@JProfiler(jKey = "DmsWeb.com.jd.loss.client.BlueDragonWebService.getLossProductByOrderId",
			jAppName = Constants.UMP_APP_NAME_DMSWEB,
			mState = {JProEnum.TP})
	@Override
	public List<LossProduct> getLossProductByOrderId(String orderId) {
		return blueDragonWebService.getLossProductByOrderId(orderId);
	}

}
