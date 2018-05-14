package com.jd.bluedragon.external.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.external.service.RefundServiceManager;
import com.jd.fa.orderrefund.RefundWebService;
import com.jd.fa.orderrefund.XmlMessage;
import com.jd.fa.refundService.CustomerRequestNew;
import com.jd.fa.refundService.RefundServiceNewSoap;
import com.jd.fa.refundService.ValidRequest;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
@Service
public class RefundServiceManagerImpl implements RefundServiceManager{
	@Autowired
	RefundWebService refundWebService;
	
	@Autowired
	RefundServiceNewSoap refundServiceSoap;
	
	@JProfiler(jKey = "DmsWeb.com.jd.fa.orderrefund.RefundWebService.sendXmlMessage",
			jAppName = Constants.UMP_APP_NAME_DMSWEB,
			mState = {JProEnum.TP,JProEnum.FunctionError})
	@Override
	public String sendXmlMessage(XmlMessage xmlMessage) {
		return refundWebService.sendXmlMessage(xmlMessage);
	}
	@JProfiler(jKey = "DmsWeb.com.jd.fa.refundService.RefundServiceSoap.innerSystemApplyForCheckWithType",
			jAppName = Constants.UMP_APP_NAME_DMSWEB,
			mState = {JProEnum.TP,JProEnum.FunctionError})
	@Override
	public ValidRequest innerSystemApplyForCheckWithType(
			CustomerRequestNew requestParam, int businessType) {
		return refundServiceSoap.innerSystemApplyForCheckWithType(requestParam, businessType);
	}
}
