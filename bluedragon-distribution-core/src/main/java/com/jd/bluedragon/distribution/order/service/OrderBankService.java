package com.jd.bluedragon.distribution.order.service;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.order.domain.OrderBankRequest;
import com.jd.bluedragon.distribution.order.domain.OrderBankResponse;
import com.jd.bluedragon.utils.PropertiesHelper;

@Service
public class OrderBankService {

	private final RestTemplate template = new RestTemplate();

	private static final String ORDER_BANK_APP_ID = "orderBank.appId";
	private static final String ORDER_BANK_APP_TOKEN = "orderBank.appToken";
	private static final String ORDER_BANK_QUERY_URI = "orderBank.query.uri";

	private static final String PATH_GET_DISCOUNT = "getDiscount";
	private static final String GET_SHOULD_PAY = "getShouldPay";
	private static final String GET_ORDER = "getOrderById";

	@SuppressWarnings({ "rawtypes" })
	public BigDecimal getDiscount(String orderId) {
		String appId = PropertiesHelper.newInstance().getValue(ORDER_BANK_APP_ID);
		String appToken = PropertiesHelper.newInstance().getValue(ORDER_BANK_APP_TOKEN);
		String uri = PropertiesHelper.newInstance().getValue(ORDER_BANK_QUERY_URI) + PATH_GET_DISCOUNT;

		this.setTimeout();
		ResponseEntity<Map> map = this.template.postForEntity(uri, new OrderBankRequest(orderId, appId, appToken),
				Map.class);

		if (map != null && map.getBody() != null) {
			return new BigDecimal(map.getBody().get("orderDiscount").toString());
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	public BigDecimal getShouldPay(String orderId, String pin) {
		String appId = PropertiesHelper.newInstance().getValue(ORDER_BANK_APP_ID);
		String appToken = PropertiesHelper.newInstance().getValue(ORDER_BANK_APP_TOKEN);
		String uri = PropertiesHelper.newInstance().getValue(ORDER_BANK_QUERY_URI) + GET_SHOULD_PAY;

		this.setTimeout();
		ResponseEntity<Map> map = this.template.postForEntity(uri, new OrderBankRequest(orderId, pin, appId, appToken),
				Map.class);

		if (map != null && map.getBody() != null) {
			return new BigDecimal(map.getBody().get("amount").toString());
		}

		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public OrderBankResponse getOrderBankResponse(String orderId) {
		String appId = PropertiesHelper.newInstance().getValue(ORDER_BANK_APP_ID);
		String appToken = PropertiesHelper.newInstance().getValue(ORDER_BANK_APP_TOKEN);
		String uri = PropertiesHelper.newInstance().getValue(ORDER_BANK_QUERY_URI) + GET_ORDER;

		this.setTimeout();
		ResponseEntity<Map> map = this.template.postForEntity(uri, new OrderBankRequest(orderId, appId, appToken),
				Map.class);

		if (map != null && map.getBody() != null) {
			OrderBankResponse response = new OrderBankResponse();
			response.setDiscount(new BigDecimal(map.getBody().get("discount").toString()));
			response.setShouldPay(new BigDecimal(map.getBody().get("shouldPay").toString()));
			
			return response;
		}

		return null;
	}

	private void setTimeout() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(500);
		factory.setReadTimeout(1000);

		this.template.setRequestFactory(factory);
	}

}
