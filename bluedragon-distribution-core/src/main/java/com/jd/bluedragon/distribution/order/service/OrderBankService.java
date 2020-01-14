package com.jd.bluedragon.distribution.order.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.order.domain.OrderBankResponse;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.orderbank.export.vo.GetDiscountReqVo;
import com.jd.orderbank.export.vo.GetOrderByIdResVo;
import com.jd.orderbank.export.vo.OrderShouldPayReqVo;
import com.jd.orderbank.export.vo.OrderShouldPayResVo;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderBankService {

	private static final String ORDER_BANK_APP_ID = "orderBank.appId";
	private static final String ORDER_BANK_APP_TOKEN = "orderBank.appToken";

	@Autowired
	private com.jd.orderbank.export.rest.OrderQueryResource orderQueryResource;

    @JProfiler(jKey = "DMS.WEB.OrderBankService.getShouldPay", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BigDecimal getShouldPay(String orderId, String pin) {
		String appId = PropertiesHelper.newInstance().getValue(ORDER_BANK_APP_ID);
		String appToken = PropertiesHelper.newInstance().getValue(ORDER_BANK_APP_TOKEN);
		OrderShouldPayReqVo reqVo = new OrderShouldPayReqVo();
		reqVo.setAppId(appId);
		reqVo.setAppToken(appToken);
		reqVo.setOrderId(orderId);
		reqVo.setPin(pin);
		
		OrderShouldPayResVo  resVo = orderQueryResource.getShouldPay(reqVo);
		if(resVo!=null){
			return resVo.getAmount();
		}
		return null;
	}

    @JProfiler(jKey = "DMS.WEB.OrderBankService.getOrderBankResponse", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public OrderBankResponse getOrderBankResponse(String orderId) {
		String appId = PropertiesHelper.newInstance().getValue(ORDER_BANK_APP_ID);
		String appToken = PropertiesHelper.newInstance().getValue(ORDER_BANK_APP_TOKEN);
		GetDiscountReqVo reqVo = new GetDiscountReqVo();
		reqVo.setAppId(appId);
		reqVo.setAppToken(appToken);
		reqVo.setOrderId(orderId);
		GetOrderByIdResVo resVo = orderQueryResource.getOrderById(reqVo);
		if(resVo!=null){
			OrderBankResponse response = new OrderBankResponse();
			response.setDiscount(resVo.getDiscount());
			response.setShouldPay(resVo.getShouldPay());
			return response;
		}
		return null;
	}
}
