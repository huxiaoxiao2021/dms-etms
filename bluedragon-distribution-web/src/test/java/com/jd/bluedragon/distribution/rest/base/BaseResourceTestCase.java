package com.jd.bluedragon.distribution.rest.base;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.BaseRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.CustomerServiceResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;

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
	public void test_waybillreturn() {
        String dmsUrl = "http://dms.etms.jd.com/services/waybillreturn/";
        String dms1Url = "http://dms1.etms.jd.com/services/waybillreturn/";
        RestTemplate template = new RestTemplate();
        String[] pickups = new String[]{"Q261279922","Q261367129","Q259760176","Q260684005","Q261217637","Q261534613","Q261445429","Q261288139","Q261308759","Q260229494","Q261066782","Q261630343","Q260958866","Q261316683","Q260686367","Q261513436","Q261178219","Q261635461","Q261584086","Q261506540","Q261319585","Q261514264","Q261605041","Q261048322","Q261438413","Q261460213","Q260637988","Q261373168","Q261499388","Q261201088","Q260595987","Q261324260","Q261083785","Q261601600","Q261259644","Q260865535","Q260765536","Q261047733","Q260867732","Q261399550","Q261216115","Q261336020","Q261320887","Q260895798","Q261441800","Q260959732","Q261266788","Q261454860","Q260511313","Q261366806","Q261326980","Q260937072","Q260708563","Q261208803","Q261185806","Q261330511","Q261273247","Q261202976","Q260578501","Q259741474","Q260326332","Q261128008","Q261624112","Q261129217","Q261063816","Q260659735","Q261654632","Q261017670","Q261326833","Q261283762","Q261389160","Q261622130","Q261387980","Q261531852","Q261014031","Q261312698","Q259554653","Q260727023","Q261037011","Q260382510","Q261154088","Q261114216","Q261122910","Q261311931","Q261549479","Q261585630","Q261701959","Q261525413","Q261307492","Q261471448","Q261353093","Q261654362","Q261363326","Q259555534","Q261309882","Q261317928","Q261828256","Q261223169","Q261708996","Q260933784"};
        int i=1;
        for(String pickup:pickups){
        	System.out.print(i++);
        	String url = dmsUrl+pickup;
        	String url1 = dms1Url+pickup;
        	CustomerServiceResponse br = template.getForObject(url, CustomerServiceResponse.class);
        	CustomerServiceResponse br1 = template.getForObject(url1, CustomerServiceResponse.class);
        	if(br.getSurfaceCode()!=null&&br1.getSurfaceCode()!=null){
        		if(!br.getSurfaceCode().equals(br1.getSurfaceCode()))
        			System.out.println("notsame:"+br.getSurfaceCode()+"_"+br1.getSurfaceCode());
        		else System.out.println("same");
        	}else{
        		System.out.println("error:"+br.getSurfaceCode()+"_"+br1.getSurfaceCode());
        	}
        }
    }

	
	
	@Test
	public void test_checkReDispatch() {
        String dmsUrl = "http://dms.etms.jd.com/services/sortingRet/checkReDispatch?packageCode=";
        String dms1Url = "http://dms1.etms.jd.com/services/sortingRet/checkReDispatch?packageCode=";
        RestTemplate template = new RestTemplate();
        String[] pickups = new String[]{"Q261279922","Q261367129","Q259760176","Q260684005","Q261217637","Q261534613","Q261445429","Q261288139","Q261308759","Q260229494","Q261066782","Q261630343","Q260958866","Q261316683","Q260686367","Q261513436","Q261178219","Q261635461","Q261584086","Q261506540","Q261319585","Q261514264","Q261605041","Q261048322","Q261438413","Q261460213","Q260637988","Q261373168","Q261499388","Q261201088","Q260595987","Q261324260","Q261083785","Q261601600","Q261259644","Q260865535","Q260765536","Q261047733","Q260867732","Q261399550","Q261216115","Q261336020","Q261320887","Q260895798","Q261441800","Q260959732","Q261266788","Q261454860","Q260511313","Q261366806","Q261326980","Q260937072","Q260708563","Q261208803","Q261185806","Q261330511","Q261273247","Q261202976","Q260578501","Q259741474","Q260326332","Q261128008","Q261624112","Q261129217","Q261063816","Q260659735","Q261654632","Q261017670","Q261326833","Q261283762","Q261389160","Q261622130","Q261387980","Q261531852","Q261014031","Q261312698","Q259554653","Q260727023","Q261037011","Q260382510","Q261154088","Q261114216","Q261122910","Q261311931","Q261549479","Q261585630","Q261701959","Q261525413","Q261307492","Q261471448","Q261353093","Q261654362","Q261363326","Q259555534","Q261309882","Q261317928","Q261828256","Q261223169","Q261708996","Q260933784"};
        int i=1;
        for(String pickup:pickups){
        	System.out.print(i++);
        	String url = dmsUrl+pickup;
        	String url1 = dms1Url+pickup;
        	SortingResponse br = template.getForObject(url, SortingResponse.class);
        	SortingResponse br1 = template.getForObject(url1, SortingResponse.class);
        	if(br.getCode()!=null&&br1.getCode()!=null){
        		if(!br.getCode().equals(br1.getCode()))
        			System.out.println("notsame:"+br.getCode()+"_"+br1.getCode());
        		else System.out.println("same");
        	}else{
        		System.out.println("error:"+br.getCode()+"_"+br1.getCode());
        	}
        }
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
