/**
 * @author wangzichen
 *
 */
package com.jd.bluedragon.distribution.rest.boundary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.PopPickupRequest;
import com.jd.bluedragon.utils.SendMailUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-core-context.xml" })
public class PopPickupResourceTestCase {
	String urlRoot = "http://localhost:8888/services";
	// String urlRoot = "http://192.168.226.157:8080/services";

	// String urlRoot = "http://dms1.etms.360buy.com/services";
	private static final Log logger = LogFactory
			.getLog(PopPickupResourceTestCase.class);

	@Test
	public void testSendMail() {
		logger.info("测试开始发送邮件...");
		
		List<String> users = new ArrayList<String>();
		users.add("zhangsan@jd.com");
		
		for (int i = 0; i < 10; i++) {
			SendMailUtil.send("title" + i, "content" + i, users);
		}
		
		List<String> users2 = new ArrayList<String>();
		users2.add("");
		users2.add(null);
		
		SendMailUtil.send("titleNull", "", users2);
		SendMailUtil.send(null, "contentNull", users2);
		SendMailUtil.send(null, null, users2);
		SendMailUtil.send("title", "content", users2);
		SendMailUtil.send(null, null, null);
		
		logger.info("测试发送邮件结束...");
	}

	@Test
	public void testWaybillNumbers() {
		PopPickupRequest popPickupRequest = new PopPickupRequest();
		popPickupRequest.setWaybillCode("494631129");

		String url = this.urlRoot + "/pop/pickUp/getPackageNumbers";
		RestTemplate template = new RestTemplate();

		try {
			String re = template.postForObject(url, popPickupRequest,
					String.class);
			System.out.println("re=" + re);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}