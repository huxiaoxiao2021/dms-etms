package com.jd.bluedragon.distribution.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.cxf.common.util.Base64Utility;
import org.junit.Test;

import com.jd.bluedragon.distribution.reverse.domain.InOrder;
import com.jd.bluedragon.distribution.reverse.domain.OrderDetail;
import com.jd.bluedragon.utils.JsonHelper;

public class SpwmsTestCase {
	@Test
	public void test1() throws Exception{
		OrderDetail d=new OrderDetail();
		d.setWareId("206484");
		d.setWareName("爱普生（Epson）ERC-38B色带 黑色（适用TM-U220A\\B\\D U200\\210\\230\\325\\370\\375\\210\\300）");
		d.setSupplierId("rb619");
		d.setIsLuxury(false);
		d.setPartCode("PB2112122110001031");
		
		d.setMainWareFunctionType("64");
		d.setMainWareAppearance("2");
		d.setMainWarePackage("1");
		d.setAttachments("8");
		
		List<OrderDetail> de=new ArrayList<OrderDetail>();
		InOrder o=new InOrder();
		o.setAimOrgId(6);
		o.setAimStoreId(1);
		o.setCreateReason("XX");
		o.setFromName("BJXINGS");
		o.setFromPin("11535");
		o.setSourceId(910);
		o.setOrderType(300);
		o.setOrderId(170313056L);
		de.add(d);
		o.setOrderDetail(de);
		String jsonString =JsonHelper.toJson(o);
		PostMethod post = new PostMethod("http://jspwmstest.360buy.com:9080/spservice/services/rest/forOut/ExpressDeliveryService/CreateIn");
		
		String contentType = "application/json";
		String charset = "UTF-8";
		RequestEntity requestEntity = new StringRequestEntity(jsonString, contentType, charset);
		post.setRequestEntity(requestEntity);
		post.addRequestHeader("Accept", contentType);
		//headedr
		//spwms.360buy.com线上需要去配置文件中获取
		SpwmsTestCase.setMethodHeaders(post, "spwms.360buy.com", "");
		
		HttpClient httpClient = new HttpClient();
		httpClient.executeMethod(post);
		if (post.getStatusCode() != 200) {
			throw new RuntimeException(post.getStatusCode() + "");
		}
		String bodyContent = post.getResponseBodyAsString();
		System.out.println("返回json结果:" + bodyContent);
		post.releaseConnection();
	}
	
	private static void setMethodHeaders(HttpMethod httpMethod, String name, String password) {
		if (httpMethod instanceof PostMethod || httpMethod instanceof PutMethod) {
			httpMethod.setRequestHeader("Content-Type", "application/json");
		}
		httpMethod.setDoAuthentication(false);
		httpMethod.setRequestHeader("Accept", "application/json");
		httpMethod.setRequestHeader("Authorization",
				"Basic " + SpwmsTestCase.base64Encode(name + ":" + password));
		
	}
	
	private static String base64Encode(String value) {
		return Base64Utility.encode(value.getBytes());
	}
	@Test
	public void test() throws Exception{
		OrderDetail d=new OrderDetail();
		d.setWareId("333");
		d.setWareName("kdk");
		d.setSupplierId("kdj");
		d.setIsLuxury(false);
		OrderDetail d1=new OrderDetail();
		d1.setWareId("333");
		d1.setWareName("kdk");
		d1.setSupplierId("kdj");
		d1.setIsLuxury(false);
		List<OrderDetail> de=new ArrayList<OrderDetail>();
		InOrder o=new InOrder();
		o.setAimOrgId(6);
		o.setAimStoreId(1);
		o.setCreateReason("10");
		o.setFromName("111");
		o.setFromPin("11");
		o.setSourceId(111);
		o.setOrderType(100);
		o.setOrderId(1111L);
		de.add(d);
		de.add(d1);
		o.setOrderDetail(de);
		String jsonString =JsonHelper.toJson(o);
		PostMethod post = new PostMethod("http://jspwmstest.360buy.com:9080/spservice/services/rest/forOut/ExpressDeliveryService/CreateIn");
		
		String contentType = "application/json";
		String charset = "UTF-8";
		RequestEntity requestEntity = new StringRequestEntity(jsonString, contentType, charset);
		post.setRequestEntity(requestEntity);
		post.addRequestHeader("Accept", contentType);
		//headedr
		//spwms.360buy.com线上需要去配置文件中获取
		SpwmsTestCase.setMethodHeaders(post, "spwms.360buy.com", "");
		
		HttpClient httpClient = new HttpClient();
		httpClient.executeMethod(post);
		if (post.getStatusCode() != 200) {
			throw new RuntimeException(post.getStatusCode() + "");
		}
		String bodyContent = post.getResponseBodyAsString();
		System.out.println("返回json结果:" + bodyContent);
		post.releaseConnection();
	}
	
}
