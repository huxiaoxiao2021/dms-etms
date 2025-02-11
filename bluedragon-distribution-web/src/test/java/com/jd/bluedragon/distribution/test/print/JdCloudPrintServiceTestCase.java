package com.jd.bluedragon.distribution.test.print;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintRequest;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintResponse;
import com.jd.bluedragon.distribution.print.service.JdCloudPrintService;
import com.jd.bluedragon.utils.JsonHelper;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration(locations = {"classpath:bak/distribution-web-print-test.xml"})
public class JdCloudPrintServiceTestCase {
	private static final Logger log = LoggerFactory.getLogger(JdCloudPrintServiceTestCase.class);
	public static void main(String[] args) throws Exception{
//		testJdClodUrl();
//		testJdClodUrlOnline();
		testJdClodUrlNew();
	}
    
	
    private static void testJdClodUrlOnline() {
    	RestTemplate template = new RestTemplate();
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "application/json; charset=UTF-8");
//		header.add("Accept", "application/json; charset=UTF-8");
		String req = "{\"template\":\"dms-b2b-m\",\"templateVer\":\"1\",\"time\":\"2019-08-13 16:20:50.946\",\"location\":\"6-6-998-\",\"orderNum\":\"ESL222118747265627943\",\"user\":\"Allen\",\"outputConfig\":[{\"type\":2,\"format\":7,\"path\":\"201908\",\"oss\":{\"accesskey\":\"1dN6I6VmTkmzN8ns\",\"secretkey\":\"ERXpRhTLpoHIoeSDthwi1uxrikNjoDRL8OBrgMwE\",\"bucket\":\"dms-print-pdfs\",\"endpoint\":\"storage.jd.com\",\"sockettimeout\":\"30000\"}}],\"model\":[{\"waybillCode\":\"JD0000000278343\",\"packageCode\":\"JD0000000278343-1-1-\",\"originalCrossCode\":\"L1\",\"originalTabletrolleyCode\":\"H01\",\"destinationCrossCode\":\"L2\",\"destinationTabletrolleyCode\":\"H02\",\"remark\":\"备注信息\",\"originalDmsName\":\"始发分拣中心\",\"destinationDmsName\":\"目的分拣中心\",\"printSiteName\":\"目的站点名称\",\"weightFlagText\":\"已称\",\"specialMark\":\"城众航\",\"specialMark1\":\"B鲜\",\"goodsPaymentText\":\"5\",\"freightText\":\"在线支付\",\"specialRequirement\":\"送货上门\",\"rodeCode\":\"20\",\"backupSiteName\":\"备用站点名\",\"packageIndex\":\"1/100\",\"packageWeight\":\"5.00kg\",\"packageSuffix\":\"1-2-3\",\"waybillCodeFirst\":\"JD000000027\",\"waybillCodeLast\":\"8343\",\"printAddress\":\"收件人地址\",\"consigneeCompany\":\"收件公司名1\",\"customerName\":\"张三\",\"telFirst\":\"130^_^\",\"telLast\":\"1001\",\"mobileFirst\":\"150^_^\",\"mobileLast\":\"1002\",\"senderCompany\":\"寄件公司名1\",\"consignerAddress\":\"寄件人地址1\",\"consignerTel\":\"170^_^1001\",\"consignerMobile\":\"180^_^1002\",\"consigner\":\"李四\",\"consignerTelText\":\"170^_^1001，180^_^1002\",\"customerOrderTime\":\"2019-08-01\",\"deliveryTimeCategory\":\"9点~11点\",\"promiseText\":\"当日达\",\"distributTypeText\":\"常温\",\"muslimSignText\":\"清真、易污染\",\"printTime\":\"2019-08-0112:00\",\"busiCode\":\"010K589\",\"busiOrderCode\":\"ESL0000010011254\",\"additionalComment\":\"http://www.jdwl.com客服电话：950616\",\"jZDFlag\":\"冷链卡班\"}]}";
		HttpEntity<String> formEntity = new HttpEntity<String>(req, header);
		log.info("开始调用云打印-online,req:{}", JsonHelper.toJson(formEntity));
		ResponseEntity<String> responseEntity = template.postForEntity("http://render.cprt.jd.com/v2/print", formEntity, String.class);
		log.info("开始调用云打印-online,resp:{}", JsonHelper.toJson(responseEntity));
	}
    private static void testJdClodUrl() {
    	RestTemplate template = new RestTemplate();
		HttpHeaders header = new HttpHeaders();
//		header.set("Content-Type", "application/json; charset=UTF-8 ;");
//		header.set("Accept", "application/json; charset=UTF-8 ;");
		header.setContentType(MediaType.APPLICATION_JSON);
		List<Charset> acceptableCharsets = new ArrayList<Charset>();
		acceptableCharsets.add(Charset.defaultCharset());
		header.setAcceptCharset(acceptableCharsets);
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		header.setAccept(acceptableMediaTypes);
		String req = "{\"template\":\"dms-b2b-m\",\"templateVer\":\"2\",\"time\":\"2019-08-13 16:20:50.946\",\"location\":\"6-6-998-\",\"orderNum\":\"ESL222118747265627943\",\"user\":\"Allen\",\"outputConfig\":[{\"type\":2,\"format\":7,\"path\":\"201908\",\"oss\":{\"accesskey\":\"XtZbEp0mKTkXNMuZ\",\"secretkey\":\"MhGEPiqrObPdP6jLU5isOH0eDDcyJwmNgVLUGH1B\",\"bucket\":\"dms-print-pdfs\",\"endpoint\":\"test.storage.jd.com\",\"sockettimeout\":\"30000\"}}],\"model\":[{\"waybillCode\":\"JD0000000278343\",\"packageCode\":\"JD0000000278343-1-1-\",\"originalCrossCode\":\"L1\",\"originalTabletrolleyCode\":\"H01\",\"destinationCrossCode\":\"L2\",\"destinationTabletrolleyCode\":\"H02\",\"remark\":\"备注信息\",\"originalDmsName\":\"始发分拣中心\",\"destinationDmsName\":\"目的分拣中心\",\"printSiteName\":\"目的站点名称\",\"weightFlagText\":\"已称\",\"specialMark\":\"城众航\",\"specialMark1\":\"B鲜\",\"goodsPaymentText\":\"5\",\"freightText\":\"在线支付\",\"specialRequirement\":\"送货上门\",\"rodeCode\":\"20\",\"backupSiteName\":\"备用站点名\",\"packageIndex\":\"1/100\",\"packageWeight\":\"5.00kg\",\"packageSuffix\":\"1-2-3\",\"waybillCodeFirst\":\"JD000000027\",\"waybillCodeLast\":\"8343\",\"printAddress\":\"收件人地址\",\"consigneeCompany\":\"收件公司名1\",\"customerName\":\"张三\",\"telFirst\":\"130^_^\",\"telLast\":\"1001\",\"mobileFirst\":\"150^_^\",\"mobileLast\":\"1002\",\"senderCompany\":\"寄件公司名1\",\"consignerAddress\":\"寄件人地址1\",\"consignerTel\":\"170^_^1001\",\"consignerMobile\":\"180^_^1002\",\"consigner\":\"李四\",\"consignerTelText\":\"170^_^1001，180^_^1002\",\"customerOrderTime\":\"2019-08-01\",\"deliveryTimeCategory\":\"9点~11点\",\"promiseText\":\"当日达\",\"distributTypeText\":\"常温\",\"muslimSignText\":\"清真、易污染\",\"printTime\":\"2019-08-0112:00\",\"busiCode\":\"010K589\",\"busiOrderCode\":\"ESL0000010011254\",\"additionalComment\":\"http://www.jdwl.com客服电话：950616\",\"jZDFlag\":\"冷链卡班\"}]}";
		HttpEntity<String> formEntity = new HttpEntity<String>(req, header);
		log.info("开始调用云打印-test,req:{}", JsonHelper.toJson(formEntity));
		ResponseEntity<String> responseEntity = template.postForEntity("http://test-render.cprt.jd.com/v2/print", formEntity, String.class);
		Charset a = responseEntity.getHeaders().getContentType().getCharSet();
		String aa = new String(responseEntity.getBody().getBytes(Charset.forName("UTF-16")),Charset.forName("UTF-8"));
		log.info("开始调用云打印-test,resp:{}", JsonHelper.toJson(responseEntity));
	}
    private static void testJdClodUrlNew() {
		String req = "{\"template\":\"dms-b2b-m\",\"templateVer\":\"2\",\"time\":\"2019-08-13 16:20:50.946\",\"location\":\"6-6-998-\",\"orderNum\":\"ESL222118747265627943\",\"user\":\"Allen\",\"outputConfig\":[{\"type\":2,\"format\":7,\"path\":\"201908\",\"oss\":{\"accesskey\":\"XtZbEp0mKTkXNMuZ\",\"secretkey\":\"MhGEPiqrObPdP6jLU5isOH0eDDcyJwmNgVLUGH1B\",\"bucket\":\"dms-print-pdfs\",\"endpoint\":\"test.storage.jd.com\",\"sockettimeout\":\"30000\"}}],\"model\":[{\"waybillCode\":\"JD0000000278343\",\"packageCode\":\"JD0000000278343-1-1-\",\"originalCrossCode\":\"L1\",\"originalTabletrolleyCode\":\"H01\",\"destinationCrossCode\":\"L2\",\"destinationTabletrolleyCode\":\"H02\",\"remark\":\"备注信息\",\"originalDmsName\":\"始发分拣中心\",\"destinationDmsName\":\"目的分拣中心\",\"printSiteName\":\"目的站点名称\",\"weightFlagText\":\"已称\",\"specialMark\":\"城众航\",\"specialMark1\":\"B鲜\",\"goodsPaymentText\":\"5\",\"freightText\":\"在线支付\",\"specialRequirement\":\"送货上门\",\"rodeCode\":\"20\",\"backupSiteName\":\"备用站点名\",\"packageIndex\":\"1/100\",\"packageWeight\":\"5.00kg\",\"packageSuffix\":\"1-2-3\",\"waybillCodeFirst\":\"JD000000027\",\"waybillCodeLast\":\"8343\",\"printAddress\":\"收件人地址\",\"consigneeCompany\":\"收件公司名1\",\"customerName\":\"张三\",\"telFirst\":\"130^_^\",\"telLast\":\"1001\",\"mobileFirst\":\"150^_^\",\"mobileLast\":\"1002\",\"senderCompany\":\"寄件公司名1\",\"consignerAddress\":\"寄件人地址1\",\"consignerTel\":\"170^_^1001\",\"consignerMobile\":\"180^_^1002\",\"consigner\":\"李四\",\"consignerTelText\":\"170^_^1001，180^_^1002\",\"customerOrderTime\":\"2019-08-01\",\"deliveryTimeCategory\":\"9点~11点\",\"promiseText\":\"当日达\",\"distributTypeText\":\"常温\",\"muslimSignText\":\"清真、易污染\",\"printTime\":\"2019-08-0112:00\",\"busiCode\":\"010K589\",\"busiOrderCode\":\"ESL0000010011254\",\"additionalComment\":\"http://www.jdwl.com客服电话：950616\",\"jZDFlag\":\"冷链卡班\"}]}";
    	String url = "http://test-render.cprt.jd.com/v2/print";
        try {
        	HttpClient httpClient = new HttpClient();
        	PostMethod method = new PostMethod(url);
            method.addRequestHeader("Content-type","application/json; charset=utf-8");  
            method.addRequestHeader("Accept", "application/json");
            method.setRequestEntity(new StringRequestEntity(req, "application/json","UTF-8"));
            log.info("开始调用云打印-test,req:{}", req);
            int statusCode = httpClient.executeMethod(method);
           if (statusCode == HttpStatus.SC_OK) {  
        	  String body = method.getResponseBodyAsString();
        	  log.info("开始调用云打印-test,resp:{}", body);
           }  
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	@Autowired
    @Qualifier("jdCloudPrintService")
    private JdCloudPrintService jdCloudPrintService;
    @Autowired
    @Qualifier("localPrintService")
    private JdCloudPrintService localPrintService;
    private Boolean useAmazon = true;
    @Test
    public void testUseNewTemplate() throws Exception{
    	//http://dmswebtest.360buy.com/sysconfig/list?pageNo=1&pageSize=10&configName=print.dmsSiteCodes.useNewTemplate
    	//测试启用新模板配置功能
    	JdCloudPrintRequest<Map<String,String>> req = jdCloudPrintService.getDefaultPdfRequest(useAmazon);
    	List<Map<String,String>> list = new ArrayList<>();
    	req.setModel(list);
    	Map<String,String> data = new HashMap<>();
    	data.put("packageCode", "JD0000000278343-1-1-");
    	data.put("waybillCode", "JD0000000278343");
    	for(int i=0;i<1;i++){
    		list.add(data);
    	}
    	req.setLocation("910");
    	req.setTemplate("dms-b2b-m");
    	req.setTemplateVer("1");
    	req.setOrderNum("56289554274");
    	JdResult<List<JdCloudPrintResponse>> result = jdCloudPrintService.jdCloudPrint(req);
    	req.setTemplate("dms-b2b-m");
    	JdResult<List<JdCloudPrintResponse>> result1 = localPrintService.jdCloudPrint(req);
    	log.warn("jdCloudPrintService:dms-b2b-m:"+JsonHelper.toJson(result));
    	log.warn("localPrintService:dms-b2b-m:"+JsonHelper.toJson(result1));
    }
//    @Test
    public void testUseNewTemplateNew() throws Exception{
    	useAmazon = true;
    	testUseNewTemplate();
    }    
}
