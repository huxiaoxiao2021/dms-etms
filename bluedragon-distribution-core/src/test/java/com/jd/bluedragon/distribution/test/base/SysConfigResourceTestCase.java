package com.jd.bluedragon.distribution.test.base;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.response.SysConfigResponse;
import com.jd.bluedragon.distribution.base.domain.SysConfig;

public class SysConfigResourceTestCase {
	private final RestTemplate template = new RestTemplate();

	@Test
	public void test_modify_sys_config() {
		String url = "http://localhost:222/services/sysconfig";

		SysConfig sysConfig = new SysConfig();

		sysConfig.setConfigName("special.password.2015");
		sysConfig.setOldPassword("111111");
		sysConfig.setNewPassword("222222");

		SysConfigResponse response = this.template.postForObject(url, sysConfig,
		        SysConfigResponse.class);

		System.out.println("code is " + response.getCode());
		System.out.println("message is " + response.getMessage());
	}
}
