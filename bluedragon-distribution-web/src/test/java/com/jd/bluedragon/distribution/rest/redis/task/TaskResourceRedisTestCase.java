package com.jd.bluedragon.distribution.rest.redis.task;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.DepartureRequest;
import com.jd.bluedragon.distribution.api.request.DepartureSendRequest;
import com.jd.bluedragon.distribution.api.request.SealBoxRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;

public class TaskResourceRedisTestCase {

	private final RestTemplate template = new RestTemplate();
	String urlRoot = "http://localhost:8280/web/services";
//	String urlRoot = "http://192.168.200.199:9195/services";

	@Test
	public void test_add_seal_box_task() {

		TaskRequest request = new TaskRequest();

		request.setType(Task.TASK_TYPE_SEAL_BOX);
		request.setSiteCode(910);
		request.setReceiveSiteCode(910);
		request.setSiteName("北京马驹桥分拣中心");
		request.setUserCode(11535);
		request.setUserName("BJXINGS");
		request.setKeyword1("910");
		request.setKeyword2("BC010F002010Y04200005051");
		request.setBusinessType(10);
		
		SealBoxRequest sealRequest1 = new SealBoxRequest();
		sealRequest1.setSiteCode(910);
		sealRequest1.setId(140);
		sealRequest1.setSiteName("北京马驹桥分拣中心");
		sealRequest1.setUserCode(11535);
		sealRequest1.setUserName("BJXINGS");
		sealRequest1.setBoxCode("BC010F002010Y04200005051");
		sealRequest1.setSealCode("09101738HL2");
		sealRequest1.setBusinessType(10);
		sealRequest1.setOperateTime("2013-10-28 01:23:05.0");

		List<SealBoxRequest> list = new ArrayList<SealBoxRequest>();
		list.add(sealRequest1);

		request.setBody(JsonHelper.toJson(list));

		String url = this.urlRoot + "/tasks";
		TaskResponse response = this.template.postForObject(url, request,
				TaskResponse.class);

		System.out.println("response :: " + response.getId());
		System.out.println("code :: " + response.getCode());
		System.out.println("message :: " + response.getMessage());
	}

	
	
	@Test
	public void test_sorting_return_task() {

		TaskRequest request = new TaskRequest();

		request.setType(Task.TASK_TYPE_RETURNS);
		request.setSiteCode(100);
		request.setReceiveSiteCode(910);
		request.setSiteName("武汉分拣中心");
		request.setUserCode(111);
		request.setUserName("测试用户");
		request.setKeyword1("100");
		request.setKeyword2("100100102");
		request.setBusinessType(10);
		request.setOperateTime("2013-10-28 01:23:05.0");

		request.setBody("[{\"shieldsError\" : \"订单取消\",  \"packageCode\" : \"100100102\",  \"id\" : 1,  \"businessType\" : 10,  \"userCode\" : 111,  \"userName\" : \"测试用户\",  \"siteCode\" : 100,  \"siteName\" : \"武汉分拣中心\",  \"operateTime\" : \"2012-03-23 18:19:54.037\"}]");

		String url = this.urlRoot + "/tasks";
		TaskResponse response = this.template.postForObject(url, request,
				TaskResponse.class);

		System.out.println("response :: " + response.getId());
		System.out.println("code :: " + response.getCode());
		System.out.println("message :: " + response.getMessage());
		
		
	}
	
	@Test
	public void testPopReceiveTaskAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试正向交接 验货 逆向 三方 b商家的
		request.setReceiveSiteCode(497);
		request.setBody("[{\"boxCodeNew\":\"\",\"boxCode\":\"9073906215\",\"quantity\":0,\"popSupName\":\"圆通快递\",\"popSupId\":463,\"crossCode\":\"463\",\"type\":0,\"queueNo\":\"024F001-2-201310190006\",\"queueType\":2,\"expressCode\":\"463\",\"expressName\":\"圆通快递\",\"id\":12451,\"businessType\":40,\"userCode\":37665,\"userName\":\"田闯\",\"siteCode\":497,\"siteName\":\"沈阳沈北分拣中心\",\"operateTime\":\"2013-10-19 10:07:54.000\"}]");
		request.setBoxCode("9073906215");
		request.setType(1030);
		request.setKeyword1("497");
		request.setKeyword2("497");
		request.setSiteCode(497);
		
		String url = this.urlRoot + "/tasks";
        
        try {
            String re = this.template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testOfflineCoreAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试正向交接 验货 逆向 三方 b商家的
		request.setReceiveSiteCode(909);
		request.setBody("[{\"taskType\":1300,\"packageCode\":\"\",\"waybillCode\":\"\",\"boxCode\":\"800427398-1-1-5\",\"receiveSiteCode\":1386,\"sealBoxCode\":\"\",\"shieldsCarCode\":\"\",\"carCode\":\"\",\"sendUserCode\":\"\",\"sendUser\":\"\",\"batchCode\":\"909-1386-201310190923140\",\"weight\":0.0,\"volume\":0.0,\"exceptionType\":\"\",\"turnoverBoxCode\":\",-1\",\"operateType\":0,\"id\":27,\"businessType\":10,\"userCode\":48442,\"userName\":\"李兵兵\",\"siteCode\":909,\"siteName\":\"上海嘉定分拣中心\",\"operateTime\":\"2013-10-19 09:23:14.000\"}]");
		request.setBoxCode("800427398-1-1-5");
		request.setType(1800);
		request.setKeyword1("909");
		request.setKeyword2("909");
		request.setSiteCode(909);
		
		String url = this.urlRoot + "/tasks";
        
        try {
            String re = this.template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testPopPickUpAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试正向交接 验货 逆向 三方 b商家的
		request.setReceiveSiteCode(1609);
		request.setBody("[{\"popBusinessCode\":\"11447\",\"popBusinessName\":\"北京1妮蕾迪服饰有限公司\",\"waybillCode\":\"494683107\",\"packageBarcode\":\"\",\"packageNumber\":1,\"isCancel\":0,\"id\":3,\"businessType\":0,\"userCode\":25208,\"userName\":\"将枫\",\"siteCode\":1609,\"siteName\":\"武汉沌口分拣中心\",\"operateTime\":\"2013-03-26 16:17:16.000\"}]");
		request.setBoxCode("");
		request.setType(1050);
		request.setKeyword1("1609");
		request.setKeyword2("1609");
		request.setSiteCode(1609);
		request.setOperateTime("");
		
		String url = this.urlRoot + "/tasks";
        
        try {
            String re = this.template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	@Test
	public void testReceiveTaskAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试收货
		request.setReceiveSiteCode(2049);
		request.setBody("[{\"shieldsCarCode\" : \"\",\"carCode\" : \"\",\"sealBoxCode\" : \"\",\"packOrBox\" : \"811508861-4-4-2\",\"turnoverBoxCode\" : \"\",\"queueNo\" : \"\",\"id\" : 5942,\"businessType\" : 10,\"userCode\" : 66542,\"userName\" : \"冯满\",\"siteCode\" : 2049,\"siteName\" : \"长沙二级分拣中心\",\"operateTime\" : \"2013-10-31 01:56:48.000\" }]");
		request.setBoxCode("");
		request.setType(1110);
		request.setKeyword1("2049");
		request.setKeyword2("811508861-4-4-2");
		request.setSiteCode(2049);
		
		String url = this.urlRoot + "/tasks";
        
        try {
            String re = this.template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testInspectionTaskAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试 验货 
		request.setReceiveSiteCode(908);
		request.setBody("[{\"sealBoxCode\" : \"\",\"boxCode\" : \"\",\"packageBarOrWaybillCode\" : \"812393010-1-1-3\",\"exceptionType\" : \"\",\"operateType\" : 0,\"receiveSiteCode\" : 0,\"id\" : 8886,\"businessType\" : 10,\"userCode\" : 63874,\"userName\" : \"邹德明\",\"siteCode\" : 908,\"siteName\" : \"广州萝岗分拣中心\",\"operateTime\" : \"2013-10-26 00:01:02.000\" }]");
		request.setBoxCode("");
		request.setType(1130);
		request.setKeyword1("908");
		request.setKeyword2("812393010-1-1-3");
		request.setSiteCode(908);
		request.setOperateTime("");
		
		String url = this.urlRoot + "/tasks";
        
        try {
            String re = this.template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testReverseRejectRedisTaskAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试 验货 
		request.setReceiveSiteCode(910);
		request.setBody("[{  \"packageCode\" : \"9934567890123456789\",  \"id\" : 2,  \"businessType\" : 1,  \"userCode\" : 1407,  \"userName\" : \"张杰梅\",  \"siteCode\" : 910,  \"siteName\" : \"北京马驹桥分拣中心\",  \"operateTime\" : \"2013-08-15 19:11:47.957\"}]");
		request.setBoxCode("");
		request.setType(3100);
		request.setKeyword1("9934567890123456789");
		request.setKeyword2("9934567890123456789");
		request.setSiteCode(910);
		request.setOperateTime("");
		
		String url = this.urlRoot + "/tasks";
        
        try {
            String re = this.template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testReverseSendRedisTaskAdd(){

		//组装请求参数
		DepartureRequest request = new DepartureRequest();
		request.setShieldsCarCode("1");
		request.setSendCarSealsID(2l);
		request.setWeight(3d);
		request.setVolume(4d);
		request.setSendUserType(0);//承运商
		request.setCarCode("6");
		request.setSendUser("7");
		request.setSendUserCode("8");
		request.setType(1);//支线发车
		request.setOldCarCode("10");
		request.setBusinessType(20);

		DepartureSendRequest dsr = new DepartureSendRequest();
		dsr.setThirdWaybillCode("123x2");
		dsr.setSendCode("1006-511-20120406140628654");
		List<DepartureSendRequest> sends = new ArrayList<DepartureSendRequest>();
		sends.add(dsr);
		request.setSends(sends);

		String url = this.urlRoot + "/departure/createDepartue";
        
        try {
            String re = this.template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testReverseSpareRedisTaskAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试 验货 
		request.setReceiveSiteCode(20156);
		request.setBody("[{   \"boxCode\" : \"TS010F005010V00100001065\",   \"receiveSiteCode\" : 25016,   \"receiveSiteName\" : \"北京3C备件库\",   \"waybillCode\" : \"817547876\",   \"isCancel\" : 0,   \"spareReason\" : \"完好\",   \"data\" : [ {     \"productId\" : \"969696\",     \"productName\" : \"Salvatore Ferragamo 菲拉格慕女士黑色/灰色双面牛皮板扣细腰带 23A758 0544101-100 GC\",     \"spareCode\" : \"PB2013090400007059\",     \"productCode\" : \"969696\",     \"productPrice\" : 1180.0,     \"arrtCode1\" : 101,     \"arrtCode2\" : 201,     \"arrtCode3\" : 303,     \"arrtCode4\" : 401,     \"arrtDesc1\" : \"商品外包装：新\",     \"arrtDesc2\" : \"主商品外观：新\",     \"arrtDesc3\" : \"主商品功能：好\",     \"arrtDesc4\" : \"附件情况：完整\"   } ],   \"id\" : 0,   \"businessType\" : 20,   \"userCode\" : 33898,   \"userName\" : \"王丽\",   \"siteCode\" : 2015,   \"siteName\" : \"北京双树分拣中心\",   \"operateTime\" : \"2013-10-30 16:40:56\" }]");
		request.setBoxCode("909-135-201310301800540");
		request.setType(3300);
		request.setKeyword1("817547876");
		request.setKeyword2("PB2013090400007059");
		request.setSiteCode(2015);
		request.setOperateTime("");
		
		String url = this.urlRoot + "/tasks";
        
        try {
            String re = this.template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testSortingRedisTaskAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试 分拣
		request.setReceiveSiteCode(40);
		request.setSiteCode(1006);
		request.setBody("[{\"receiveSiteCode\" : 40,\"receiveSiteName\" : \"黄浦站\",  \"boxCode\" : \"100039104N2S3H1\",  \"packageCode\" : \"100039104N2S3H1\",  \"isCancel\" : 0,  \"id\" : 6,  \"businessType\" : 10,  \"userCode\" : 6781,  \"userName\" : \"程岩\",  \"siteCode\" : 1006,  \"siteName\" : \"影子-分拣中心\",  \"operateTime\" : \"\"}]");
		request.setBoxCode("100039104N2S3H1");
		request.setType(1200);
		request.setKeyword1("1006");
		request.setKeyword2("100039104N2S3H1");
		request.setOperateTime("");
		
		String url = this.urlRoot + "/tasks";
        
        try {
            String re = this.template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testWaybillStatusRedisTaskAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试 运单状态相关任务
//		request.setReceiveSiteCode(40);
		request.setSiteCode(511);
		request.setBody("{  \"waybillCode\" : \"10000014853\",  \"packageCode\" : \"10000014853\",  \"orgId\" : 6,  \"orgName\" : \"总公司\",  \"createSiteCode\" : 511,  \"createSiteType\" : 64,  \"createSiteName\" : \"北京大鲁店分拣中心\",  \"receiveSiteCode\" : 39,  \"receiveSiteType\" : 16,  \"receiveSiteName\" : \"石景山站\",  \"operatorId\" : 1407,  \"operator\" : \"张杰梅\",  \"operateType\" : 300,  \"operateTime\" : 1385966350000}");
		request.setBoxCode("KD123456789");
		request.setType(9999);
		request.setKeyword1("5");
		request.setKeyword2("10");
		request.setOperateTime("");
		
		String url = this.urlRoot + "/tasks";
        
        try {
            String re = this.template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testPartnerWaybillSynchroRedisTaskAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试 运单状态相关任务
//		request.setReceiveSiteCode(40);
//		request.setSiteCode(511);
		request.setBody("{  \"partnerWaybillCode\" : \"121324111444\",  \"waybillCode\" : \"000000053826\",  \"packageBarcode\" : \"000000053826-1-1-\",  \"partnerSiteCode\" : 1379,  \"createUser\" : \"BJLJJ\",  \"createUserCode\" : 3000970,  \"createTime\" : 1348898465810,  \"createSiteCode\" : 511,  \"updateTime\" : 1348898416527}");
//		request.setBoxCode("KD123456789");
		request.setType(1601);
		request.setKeyword1("000000053826");
		request.setKeyword2("121324111444");
		request.setOperateTime("");
		
		String url = this.urlRoot + "/tasks";
        
        try {
            String re = this.template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testWaybillTrackRedisTaskAdd(){
		TaskRequest request = new TaskRequest();
		
		//测试 运单状态相关任务
//		request.setReceiveSiteCode(40);
		request.setSiteCode(2015);
		request.setBody("{   \"boxCode\" : \"BC021F004010F00500123093\",   \"packageCode\" : \"000037786991-1-1-\",   \"orgId\" : 6,   \"orgName\" : \"总公司\",   \"createSiteCode\" : 2015,   \"createSiteType\" : 64,   \"createSiteName\" : \"北京双树分拣中心\",   \"operatorId\" : 79197,   \"operator\" : \"刘冰\",   \"operateType\" : 400,   \"operateTime\" : 1386951004000 }");
//		request.setBoxCode("KD123456789");
		request.setType(6666);
//		request.setKeyword1("");
		request.setKeyword2("000037786991-1-1-");
		request.setOperateTime("");
		
		String url = this.urlRoot + "/tasks";
        
        try {
            String re = this.template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
