package com.jd.bluedragon.distribution.waybill.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.utils.PropertiesHelper;

@Service
public class WaybillCancelService {

	private static final String IS_RERUND_WAYBILL = "29303";
	private static final String DMSVER_URI = "dmsver.uri";
	

	private final RestTemplate template = new RestTemplate();

	@SuppressWarnings({ "rawtypes" })
	public Boolean isRefundWaybill(String waybillCode) {
		String uri = PropertiesHelper.newInstance().getValue(DMSVER_URI) + waybillCode;

		this.setTimeout();
		
		ResponseEntity<Map> map = this.template.getForEntity(uri, Map.class);
		if (map != null && map.getBody() != null && map.getBody().get("code") != null
				&& map.getBody().get("code").toString().equals(IS_RERUND_WAYBILL)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	private void setTimeout() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(500);
		factory.setReadTimeout(3000);

		this.template.setRequestFactory(factory);
	}
	
}