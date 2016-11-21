package com.jd.bluedragon.distribution.rest.base;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.BaseRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;

public class BaseResourceTestCase {

	static String baseUrl = "http://localhost:8080/bluedragon-distribution-web/services/bases/";
	
	@Test
	public void test() throws Exception{
		test_login("123", "123");
		test_checkallsite();
		test_site("39");
		test_driver("17963");
		test_car("京B23425");
		test_center("510");
		test_carByOrg("6");
		test_org();
		test_siteByOrg("6");
		test_errorList();
	}
	

	@Test
	public void test_login() {
        String loginUrl = "http://localhost:8080/services/bases/login";
        RestTemplate template = new RestTemplate();
        BaseRequest bq = new BaseRequest();
        bq.setErpAccount("3pl_test");
        bq.setPassword("1234abcd");
        BaseResponse br = template.postForObject(loginUrl, bq, BaseResponse.class);
        Assert.assertTrue(br.getCode() == 500);
        System.out.println(JsonHelper.toJson(br));


    }

	
	public static void test_login(String use,String pwd){
		String urlRoot =baseUrl + "login";
		BaseRequest bq = new BaseRequest();
		bq.setErpAccount(use);
		bq.setPassword(pwd);
		RestTemplate template = new RestTemplate();		
		BaseResponse br = template.postForObject(urlRoot, bq, BaseResponse.class);
		System.out.println("用户登录消息 :" + br.getMessage());
	}
	
	public static void test_checkallsite(){
		String urlRoot = baseUrl + "allsite";
		RestTemplate template = new RestTemplate();
		List<Map> list = (List<Map>)template.getForObject(urlRoot, List.class);		
		System.out.print("查询全部站点 size:" + list.size());
	}
	
	public static void test_site(String code){
		String urlRoot = baseUrl + "site/" + code;
		RestTemplate template = new RestTemplate();
		BaseResponse r = (BaseResponse)template.getForObject(urlRoot, BaseResponse.class);
		System.out.println("查询站点" + code + "：" + r.getMessage());
	}
	
	public static void test_driver(String code){
		String urlRoot = baseUrl + "driver/" + code;
		RestTemplate template = new RestTemplate();
		BaseResponse r = (BaseResponse)template.getForObject(urlRoot, BaseResponse.class);
		System.out.println("查询司机" + code + "：" + r.getMessage());
	}
	
	public static void test_car(String code){
		String urlRoot = baseUrl + "vehicle?vehicleCode=" + code;
		RestTemplate template = new RestTemplate();
		BaseResponse r = (BaseResponse)template.getForObject(urlRoot, BaseResponse.class);
		System.out.println("查询车牌号" + code + "：" + r.getMessage());
	}
	
	public static void test_center(String code){
		String urlRoot = baseUrl + "sortingcenter/" + code;
		RestTemplate template = new RestTemplate();
		BaseResponse r = (BaseResponse)template.getForObject(urlRoot, BaseResponse.class);
		System.out.println("查询分拣中心" + code + "：" + r.getMessage());
	}
	
	public static void test_carByOrg(String code){
		String urlRoot = baseUrl + "cars/" + code;
		RestTemplate template = new RestTemplate();
		List<Map> list = (List<Map>)template.getForObject(urlRoot, List.class);		
		System.out.println("查询机构下车辆" + code + "：" + list.size());
	}
	
	public static void test_org(){
		String urlRoot = baseUrl + "allorgs";
		RestTemplate template = new RestTemplate();
		List<Map> list = (List<Map>)template.getForObject(urlRoot, List.class);		
		System.out.print("查询全部机构 size:" + list.size());
	}
	
	public static void test_siteByOrg(String code){
		String urlRoot = baseUrl + "sites/" + code;
		RestTemplate template = new RestTemplate();
		List<Map> list = (List<Map>)template.getForObject(urlRoot, List.class);	
		System.out.println("查询机构下站点" + code + "：" + list.size());
	}
	
	public static void test_errorList(){
		String urlRoot = baseUrl + "errorlist/";
		RestTemplate template = new RestTemplate();
		List<Map> list = (List<Map>)template.getForObject(urlRoot, List.class);	
		System.out.println("查询错误类型：" + list.size());
	}
}
