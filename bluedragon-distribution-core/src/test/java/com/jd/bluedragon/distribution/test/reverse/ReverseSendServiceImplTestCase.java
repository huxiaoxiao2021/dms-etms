package com.jd.bluedragon.distribution.test.reverse;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.reverse.domain.Product;
import com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSendAsiaWms;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSendWms;
import com.jd.bluedragon.utils.XmlHelper;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReverseSendServiceImplTestCase {
	
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public com.jd.staig.receiver.rpc.DataReceiver getDtcDataReceiverService() {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                "/spring/distribution-core-jsf.xml");
        com.jd.staig.receiver.rpc.DataReceiver service = (com.jd.staig.receiver.rpc.DataReceiver) appContext
                .getBean("dtcDataReceiverServiceJsf");
        return service;
    }

	public static void addMapWms(Map<String, String> m, String a, String b) {
		if (m.containsKey(a)) {
			//如果有重复包裹去重、
			if(!m.get(a).contains(b)){
				m.put(a, m.get(a) + "," + b);
			}
		} else {
			m.put(a, b);
		}
	}
	
	public static void addMapWmsOld(Map<String, String> m, String a, String b) {
		if (m.containsKey(a)) {
			//如果有重复包裹去重、
			if(!a.contains(b)){
				m.put(a, m.get(a) + "," + b);
			}
		} else {
			m.put(a, b);
		}
	}
	
	@Test
	public void testSendAsiaWMS() {
		ReverseSendAsiaWms send = null;
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ReturnRequest><cky2>3</cky2><isInStore>1</isInStore><lossQuantity>0</lossQuantity><operateTime>2014-08-26 16:46:11</operateTime><orderId>1786592045</orderId><orgId>3</orgId><packageCodes>1786592045N1S1H110</packageCodes><productRequests><ProductRequest><productId>1092126</productId><productLoss>0</productLoss><productName>金典（GOLDEN） GD-50M 财务装订机</productName><productNum>1</productNum><productPrice>0</productPrice></ProductRequest></productRequests><sendCode>Y3011-47064-201408261640530</sendCode><storeId>80</storeId><userName>郭玉海</userName></ReturnRequest>";
		
		send = XmlHelper.xmlToObject(xml, ReverseSendAsiaWms.class, null);
		send.setOrderSum(12);
		send.setPackSum(133);
		String messageValue = XmlHelper.toXml(send, ReverseSendAsiaWms.class);
		
		System.out.println(messageValue);
	}
	
	@Test
	public void testSendXml2WMS() {
		ReceiveRequest request = null;
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ReceiveRequest><sendCode>JPI00000000000000464398046533152</sendCode><orderId>ESL4398047079402</orderId><receiveType>1</receiveType><operateTime>2015-07-22 11:44:42</operateTime><userName>bjcwh</userName><canReceive>0</canReceive><rejectCode>100</rejectCode><rejectMessage>原因不明</rejectMessage></ReceiveRequest>";
		
		request = XmlHelper.xmlToObject(xml, ReceiveRequest.class, null);

		Assert.assertEquals("ESL4398047079402", request.getOrderId());
	}
	
	@Test
	public void testReverseSendWms() {

		ReverseSendWms sendAssert = new ReverseSendWms();
		sendAssert.setCky2(3);
		sendAssert.setIsInStore(1);
		sendAssert.setLossQuantity(0);
		sendAssert.setOperateTime("2014-08-26 16:46:11");
		sendAssert.setOrderId("1786592045");
		sendAssert.setOrgId(3);
		sendAssert.setPackageCodes("1786592045N1S1H110");
		List<Product> proList = new ArrayList<Product>();
		Product p = new Product();
		p.setProductId("1092126");
		p.setProductLoss("0");
		p.setProductName("金典（GOLDEN） GD-50M 财务装订机");
		p.setProductNum(1);
		p.setProductPrice("12");
		proList.add(p);
		sendAssert.setProList(proList);
		sendAssert.setSendCode("Y3011-47064-201408261640530");
		sendAssert.setStoreId(80);
		sendAssert.setUserName("郭玉海");
		sendAssert.setType(12346);
		sendAssert.setWaybillSign("waybillsi");
		sendAssert.setSourceCode("1245");
		
		sendAssert.setReverseCode(10);
		sendAssert.setReverseReason("打包不全");
		sendAssert.setBusiOrderCode("45678");
		String messageValue = XmlHelper.toXml(sendAssert, ReverseSendWms.class);
		log.info(messageValue);
		System.out.println(messageValue);
		ReverseSendWms send = new ReverseSendWms();
		send =  (ReverseSendWms) XmlHelper.toObject(messageValue, ReverseSendWms.class);

		Assert.assertEquals(null, send.getReverseCode());
		Assert.assertEquals(null, send.getReverseReason());
		Assert.assertEquals(null, send.getBusiOrderCode());
	}
	
	
	@Test
	public void testReverseSendAsiaWms() {

		ReverseSendAsiaWms sendAssert = new ReverseSendAsiaWms();
		sendAssert.setCky2(3);
		sendAssert.setIsInStore(1);
		sendAssert.setLossQuantity(0);
		sendAssert.setOperateTime("2014-08-26 16:46:11");
		sendAssert.setOrderId("T1786592045");
		sendAssert.setOrgId(3);
		sendAssert.setPackageCodes("1786592045N1S1H110");
		List<Product> proList = new ArrayList<Product>();
		Product p = new Product();
		p.setProductId("1092126");
		p.setProductLoss("0");
		p.setProductName("金典（GOLDEN） GD-50M 财务装订机");
		p.setProductNum(1);
		p.setProductPrice("12");
		proList.add(p);
		sendAssert.setProList(proList);
		sendAssert.setSendCode("Y3011-47064-201408261640530");
		sendAssert.setStoreId(80);
		sendAssert.setUserName("郭玉海");
		sendAssert.setType(12346);
		sendAssert.setOrderSum(12);
		sendAssert.setPackSum(133);
		
		sendAssert.setReverseCode(10);
		sendAssert.setReverseReason("打包不全");
		sendAssert.setBusiOrderCode("1786592045");
		String messageValue = XmlHelper.toXml(sendAssert, ReverseSendAsiaWms.class);
		log.info(messageValue);
		
		ReverseSendAsiaWms send = new ReverseSendAsiaWms();
		send =  (ReverseSendAsiaWms) XmlHelper.toObject(messageValue, ReverseSendAsiaWms.class);

		Assert.assertEquals(null, send.getReverseCode());
		Assert.assertEquals(null, send.getReverseReason());
		Assert.assertEquals(null, send.getBusiOrderCode());
	}
	
	@Test
	public void testDtcReceiverInterWms() {
		ReverseSendWms send = new ReverseSendWms();
		send.setCky2(6);
		send.setIsInStore(1);
		send.setLossQuantity(0);
		send.setOperateTime("2014-08-26 16:46:11");
		send.setOrderId("1786592078");
		send.setOrgId(6);
		send.setPackageCodes("1786592078-1-1-110");
		List<Product> proList = new ArrayList<Product>();
		Product p = new Product();
		p.setProductId("1092126");
		p.setProductLoss("0");
		p.setProductName("金典（GOLDEN） GD-50M 财务装订机");
		p.setProductNum(1);
		p.setProductPrice("12");
		proList.add(p);
		send.setProList(proList);
		send.setSendCode("3011-47064-201408261640530");
		send.setStoreId(5);
		send.setUserName("郭玉海");
		send.setType(12346);
		send.setWaybillSign("waybillsi");
		send.setSourceCode("1245");
		
		String target = send.getOrgId() + "," + send.getCky2() + "," + send.getStoreId();

		String messageValue = XmlHelper.toXml(send, ReverseSendWms.class);
		String outboundNo = send.getOrderId();
		String outboundType = "OrderBackDl"; // OrderBackDl
		String source = "DMS";
		log.error("target:{}", target);
		log.error("outboundType:{}",outboundType);
		log.error("priority:{}",2);
		log.error("messageValue:{}", messageValue);
		log.error("messageMd5Value:{}", "null");
		log.error("source:{}", source);
		log.error("outboundNo:{}", outboundNo);
		/**
		 * 
		 * 乔洪佥  15:17:13 
			这算3.0的库房么?
			黄亮  15:17:29 
			我这里区分不了库房
			乔洪佥  15:17:39 
			区分不了是吧
			那就是所有的库房你都是这样传值了是吧
			①②均使用原接口中outboundType字段
		 */
		com.jd.staig.receiver.rpc.Result result =  getDtcDataReceiverService().downStreamHandle(target, outboundType, outboundType, 2, messageValue, null,
				source, outboundNo);
		Assert.assertEquals(1, result.getResultCode());
		
	}
	
	@Test
	public void testDtcReceiverInterAsiaWms() {
		ReverseSendAsiaWms send = new ReverseSendAsiaWms();
		send.setCky2(6);
		send.setIsInStore(5);
		send.setLossQuantity(0);
		send.setOperateTime("2014-08-26 16:46:11");
		send.setOrderId("1786592077");
		send.setOrgId(6);
		send.setPackageCodes("1786592077N1S1H110");
		List<Product> proList = new ArrayList<Product>();
		Product p = new Product();
		p.setProductId("1092126");
		p.setProductLoss("0");
		p.setProductName("金典（GOLDEN） GD-50M 财务装订机");
		p.setProductNum(1);
		p.setProductPrice("12");
		proList.add(p);
		send.setProList(proList);
		send.setSendCode("Y3011-47064-201408261640530");
		send.setStoreId(5);
		send.setUserName("郭玉海");
		send.setType(12346);
		send.setOrderSum(12);
		send.setPackSum(133);
		
		String target = send.getOrgId() + "," + send.getCky2() + "," + send.getStoreId();

		String messageValue = XmlHelper.toXml(send, ReverseSendAsiaWms.class);
		String outboundNo = send.getOrderId();
		String outboundType = "OrderBackDl"; // OrderBackDl
		String source = "DMS";
		log.error("target:{}",target);
		log.error("outboundType:{}", outboundType);
		log.error("priority:{}",2);
		log.error("messageValue:{}", messageValue);
		log.error("messageMd5Value:{}","null");
		log.error("source:{}",source);
		log.error("outboundNo:{}",outboundNo);
		/**
		 * 
		 * 乔洪佥  15:17:13 
			这算3.0的库房么?
			黄亮  15:17:29 
			我这里区分不了库房
			乔洪佥  15:17:39 
			区分不了是吧
			那就是所有的库房你都是这样传值了是吧
			①②均使用原接口中outboundType字段
		 */
		com.jd.staig.receiver.rpc.Result result =  getDtcDataReceiverService().downStreamHandle(target, outboundType, outboundType, 2, messageValue, null,
				source, outboundNo);
		Assert.assertEquals(1, result.getResultCode());
		
	}
	/**
	 * @param args
	 */
	public static void main(String args[]){
		Map<String, String> m = new HashMap<String, String>();
		Map<String, String> n = new HashMap<String, String>();
		String a = "a";
		String b = "b";
		String[] as = {"1","1","2"};
		for(String asi:as){
			addMapWms(m, a, asi);
			addMapWmsOld(n, a, asi);
		}
		for(String asi:as){
			addMapWms(m, b, asi);
			addMapWmsOld(n, b, asi);
		}
		System.out.println(m.toString());
		System.out.println(n.toString());
		
//		ReverseSendAsiaWms send = new ReverseSendAsiaWms();
		ReverseSendWms send = new ReverseSendWms();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ReturnRequest><cky2>3</cky2><isInStore>1</isInStore><lossQuantity>0</lossQuantity><operateTime>2014-08-26 16:46:11</operateTime><orderId>1786592045</orderId><orgId>3</orgId><packageCodes>1786592045N1S1H110</packageCodes><productRequests><ProductRequest><productId>1092126</productId><productLoss>0</productLoss><productName>金典（GOLDEN） GD-50M 财务装订机</productName><productNum>1</productNum><productPrice>0</productPrice></ProductRequest></productRequests><sendCode>Y3011-47064-201408261640530</sendCode><storeId>80</storeId><userName>郭玉海</userName></ReturnRequest>";
		//send = XmlHelper.xmlToObject(xml, ReverseSendAsiaWms.class, null);
		send.setCky2(3);
		send.setIsInStore(1);
		send.setLossQuantity(0);
		send.setOperateTime("2014-08-26 16:46:11");
		send.setOrderId("1786592045");
		send.setOrgId(3);
		send.setPackageCodes("1786592045N1S1H110");
		List<Product> proList = new ArrayList<Product>();
		Product p = new Product();
		p.setProductId("1092126");
		p.setProductLoss("0");
		p.setProductName("金典（GOLDEN） GD-50M 财务装订机");
		p.setProductNum(1);
		p.setProductPrice("12");
		proList.add(p);
		send.setProList(proList);
		send.setSendCode("Y3011-47064-201408261640530");
		send.setStoreId(80);
		send.setUserName("郭玉海");
		send.setType(12346);
		send.setWaybillSign("waybillsi");
		send.setSourceCode("1245");
//		send.setOrderSum(12);
//		send.setPackSum(133);
		
//		send.setReverseCode(10);
//		send.setReverseReason("打包不全");
		String messageValue = XmlHelper.toXml(send, ReverseSendWms.class);
		String jsonx = JsonHelper.toJson(send);
		System.out.println(messageValue);
		System.out.println(jsonx);
		
	}
	


}
