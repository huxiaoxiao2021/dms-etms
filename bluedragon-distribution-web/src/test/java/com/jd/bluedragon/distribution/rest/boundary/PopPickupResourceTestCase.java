/**
 * @author wangzichen
 *
 */
package com.jd.bluedragon.distribution.rest.boundary;

import com.jd.bluedragon.distribution.api.request.PopPickupRequest;
import com.jd.bluedragon.utils.SendMailUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-core-context.xml" })
public class PopPickupResourceTestCase {
	String urlRoot = "http://localhost:8888/services";
	// String urlRoot = "http://192.168.226.157:8080/services";

	// String urlRoot = "http://dms1.etms.360buy.com/services";
	private static final Logger log = LoggerFactory.getLogger(PopPickupResourceTestCase.class);

	@Test
	public void testSendMail() {
		log.info("测试开始发送邮件...");
		
		List<String> users = new ArrayList<String>();
		users.add("zhangsan@jd.com");
		
		for (int i = 0; i < 10; i++) {
			SendMailUtil.sendSimpleEmail("title" + i, "content" + i, users);
		}
		
		List<String> users2 = new ArrayList<String>();
		users2.add("");
		users2.add(null);
		
		SendMailUtil.sendSimpleEmail("titleNull", "", users2);
		SendMailUtil.sendSimpleEmail(null, "contentNull", users2);
		SendMailUtil.sendSimpleEmail(null, null, users2);
		SendMailUtil.sendSimpleEmail("title", "content", users2);
		SendMailUtil.sendSimpleEmail(null, null, null);
		
		log.info("测试发送邮件结束...");
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