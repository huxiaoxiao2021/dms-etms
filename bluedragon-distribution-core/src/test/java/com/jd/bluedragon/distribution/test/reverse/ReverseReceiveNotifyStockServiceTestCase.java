package com.jd.bluedragon.distribution.test.reverse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.jd.bluedragon.core.message.consumer.reverse.ReversePopConsumer;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveNotifyStockService;
import com.jd.etms.message.Message;

import junit.framework.Assert;

public class ReverseReceiveNotifyStockServiceTestCase {
	
    private final Log logger = LogFactory.getLog(this.getClass());
	
	@Test
	public void testReceive_canreceive1() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ReceiveRequest><sendCode>24965235</sendCode><orderId>12023641757</orderId>"
				+ "<operateTime>2016-02-16 13:08:56</operateTime><userCode>wangxuan30</userCode><userName>wangxuan30|王璇</userName><receiveType>3</receiveType>"
				+ "  <canReceive>1</canReceive></ReceiveRequest>";
		ReverseReceiveNotifyStockService service = new ReverseReceiveNotifyStockService();
		String waybillCode = service.receive(xml).toString();
		
		Assert.assertEquals("12023641757", waybillCode);
	}
	
	@Test
	public void testReceive_canreceive0() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ReceiveRequest>    <sendCode>24965235</sendCode>    <orderId>344584322</orderId>    <receiveType>3</receiveType>    <operateTime>2012-10-19 17:24:20</operateTime>    <userName>收货人</userName>    <userCode>123</userCode>    <orgId>6</orgId>    <storeId>1</storeId>    <canReceive>0</canReceive>    <rejectCode>100</rejectCode>    <rejectMessage>货物破损</rejectMessage></ReceiveRequest>";
		ReverseReceiveNotifyStockService service = new ReverseReceiveNotifyStockService();
		String waybillCode = service.receive(xml).toString();
		
		Assert.assertEquals("-1", waybillCode);
	}
	
	@Test
	public void testReceive_ReversePopConsumer_consume0() throws Throwable{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ReceiveRequest>    <sendCode>24965235</sendCode>    <orderId>344584322</orderId>    <receiveType>3</receiveType>    <operateTime>2012-10-19 17:24:20</operateTime>    <userName>收货人</userName>    <userCode>123</userCode>    <orgId>6</orgId>    <storeId>1</storeId>    <canReceive>0</canReceive>    <rejectCode>100</rejectCode>    <rejectMessage>货物破损</rejectMessage></ReceiveRequest>";
		Message msg = new Message();
		msg.setContent(xml);
		ReversePopConsumer service = new ReversePopConsumer();
		//service.consume(msg);
	}
	
	@Test
	public void testReceive_ReversePopConsumer_consume1() throws Throwable{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ReceiveRequest><sendCode>24965235</sendCode><orderId>12023641757</orderId>"
				+ "<operateTime>2016-02-16 13:08:56</operateTime><userCode>wangxuan30</userCode><userName>wangxuan30|王璇</userName><receiveType>3</receiveType>"
				+ "  <canReceive>1</canReceive></ReceiveRequest>";
		Message msg = new Message();
		msg.setContent(xml);
		ReversePopConsumer service = new ReversePopConsumer();
		//service.consume(msg);
	}
}
