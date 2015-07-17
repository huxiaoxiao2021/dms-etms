package com.jd.bluedragon.distribution.test.popJoin;

import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.response.PopJoinResponse;
import com.jd.bluedragon.distribution.rest.pop.PopJoinResource;
import com.jd.bluedragon.utils.DateHelper;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-15 下午09:49:36
 * 
 *             POP收货交接清单
 */
public class PopJoinRestTest {

	public static final String urlRoot = "http://192.168.226.157:8080/services/";

	public static void main(String[] args) {
		
		test_queryPopJoinList();
		test_queryBusiList();
	}
	
	@SuppressWarnings("unchecked")
	public static void test_queryPopJoinList() {
		PopJoinResource.PopJoinQuery popJoinQuery = new PopJoinResource.PopJoinQuery();
		popJoinQuery.setCreateSiteCode(1006);
		popJoinQuery.setStartTime(DateHelper.parseDateTime("2012-10-19 15:00:00"));
		popJoinQuery.setEndTime(DateHelper.parseDateTime("2012-10-19 24:00:00"));
		popJoinQuery.setPopSupName("湖北");
		
		Pager pager = new Pager();
		popJoinQuery.setPager(pager);

		RestTemplate template = new RestTemplate();
		String url = urlRoot + "popJoin/queryPopJoinList";
		PopJoinResponse<PopJoinResource.PopJoinQuery> response = template.postForObject(url,
				popJoinQuery, PopJoinResponse.class);
		System.out.println(response);
	}
	
	@SuppressWarnings("unchecked")
	public static void test_queryBusiList() {
		PopJoinResource.PopJoinQuery popJoinQuery = new PopJoinResource.PopJoinQuery();
		popJoinQuery.setBusiName("红豆");
		
		Pager pager = new Pager();
		popJoinQuery.setPager(pager);

		RestTemplate template = new RestTemplate();
		String url = urlRoot + "popJoin/queryBusiList";
		PopJoinResponse<PopJoinResource.PopJoinQuery> response = template.postForObject(url,
				popJoinQuery, PopJoinResponse.class);
		System.out.println(response);
	}
}
