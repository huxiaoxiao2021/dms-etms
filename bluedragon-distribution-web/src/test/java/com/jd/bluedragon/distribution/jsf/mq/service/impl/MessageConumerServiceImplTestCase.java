package com.jd.bluedragon.distribution.jsf.mq.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jd.bluedragon.core.message.consumer.reverse.PickWareConsumer;
import com.jd.bluedragon.core.message.consumer.sendCar.SendCarContext;
import com.jd.bluedragon.distribution.reverse.domain.PickWare;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.erp.service.domain.BaseEntity;
import com.jd.etms.erp.service.dto.SendInfoDto;
import com.jd.etms.finance.wss.pojo.ResponseMessage;
import com.jd.etms.finance.wss.pojo.SortingCar;
import com.jd.etms.message.Consumer;
import com.jd.etms.message.DestinationType;
import com.jd.etms.message.Message;

import junit.framework.Assert;

public class MessageConumerServiceImplTestCase {

	private final static Logger LOGGER = LoggerFactory.getLogger(MessageConumerServiceImplTestCase.class);
	
	
	@Test
	public void testConsume_VosSendCar(){
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"/distribution-web-jsf-client-test.xml");

		Consumer service = (Consumer) appContext
				.getBean("jsfMessageConumerService");
		LOGGER.info("得到调用端代理：{jsfMessageConumerService}", service);

		Message message = new Message();
		message.setDestinationCode("vos_send_car");
		message.setConnectionSystemId("BlueDragonDistribution");
		message.setContent("{\"waybillCode\": \"123456789\",  \"siteCode\": \"00752\",  \"siteType\": \"4\",  \"lineCode\": \"T11010F008010F6010000\",  \"opeTime\": \"2016-10-11 13:01:00.331\", \"batchCode\": \"511-39-20141204154759781\",  \"batchType\": \"1\"}");
        SendCarContext context=null;
        context= JsonHelper.fromJsonUseGson(message.getContent(), SendCarContext.class);
        System.out.printf("111111111111111111111"+context.getOpeTime());
        message.setDestinationType(DestinationType.TOPIC);

		try {
			service.consume(message);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public void testConsume_VosCancelSend(){
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"/distribution-web-jsf-client-test.xml");

		Consumer service = (Consumer) appContext
				.getBean("jsfMessageConumerService");
		LOGGER.info("得到调用端代理：{jsfMessageConumerService}", service);

		Message message = new Message();
		message.setDestinationCode("vos_cancel_send");
		message.setConnectionSystemId("BlueDragonDistribution");
		message.setContent("{\"waybillCode\": \"123456789\",  \"siteCode\": \"00752\",  \"siteType\": \"4\",  \"lineCode\": \"T11010F008010F6010000\",  \"opeTime\": \"2016-10-11 13:01:00.331\", \"batchCode\": \"511-39-20141204154759781\",  \"batchType\": \"1\"}");
        SendCarContext context=null;
        context= JsonHelper.fromJsonUseGson(message.getContent(), SendCarContext.class);
        System.out.printf("111111111111111111111"+context.getOpeTime());
        message.setDestinationType(DestinationType.TOPIC);

		try {
			service.consume(message);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testConsume_mutiMq(){
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"/distribution-web-jsf-client-test.xml");

		Consumer service = (Consumer) appContext
				.getBean("jsfMessageConumerService");
		LOGGER.info("得到调用端代理：{jsfMessageConumerService}", service);

		Message message = new Message();
		message.setDestinationCode("bd_dms_reverse_receive");
		message.setConnectionSystemId("BlueDragonDistributionStock");
		message.setContent("msg test!");
		try {
			service.consume(message);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
}
	
	@Test
	public void testWaybillServiceJSF(){
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"/distribution-web-jsf-client-test.xml");

		com.jd.etms.finance.wss.WaybillDataServiceWS waybillDataServiceWS = (com.jd.etms.finance.wss.WaybillDataServiceWS) appContext
				.getBean("waybillServiceJSF");
		try {
			List<SortingCar> sortingCarAl = new ArrayList<SortingCar>();
			SortingCar scar = new SortingCar();
			scar.setCarrierId(1);
			scar.setSortBatchNo("123-123");
			scar.setSortCarId(74110);
			scar.setSortingCenterId(910);
			scar.setTargetSiteId(3011);
			scar.setVolume(100);
			scar.setWeight(200);
			sortingCarAl.add(scar);
			ResponseMessage responseMessage = waybillDataServiceWS
					.sendSortingCar(sortingCarAl);
			LOGGER.info(responseMessage.getIsSuccess()+"");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testBizServiceInterfaceJsf(){
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"/distribution-web-jsf-client-test.xml");

		com.jd.etms.erp.ws.BizServiceInterface bizServiceInterfaceJsf = (com.jd.etms.erp.ws.BizServiceInterface) appContext
				.getBean("bizServiceInterfaceJsf");
		
		BaseEntity<Boolean> response = null;
		try {
			response = bizServiceInterfaceJsf.checkReDispatch("1000000002");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals(true, response.getData().booleanValue());
	}
	
	@Test
	public void testSupportServiceJsf(){
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"/distribution-web-jsf-client-test.xml");

		com.jd.etms.erp.ws.SupportServiceInterface supportServiceJsf = (com.jd.etms.erp.ws.SupportServiceInterface) appContext
				.getBean("supportServiceJsf");
		BaseEntity<List<SendInfoDto>> response =null;
		try {
			List<SortingCar> sortingCarAl = new ArrayList<SortingCar>();
			SendInfoDto dto = new SendInfoDto();
			dto.setBoxCode("153636597N1S1HA");
			response = supportServiceJsf.getSendDetails(dto);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		Assert.assertEquals(true,response.getData().size()>0);
	}
	
	@Test
	public void testConsume_sph_reverse_1(){
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				"/distribution-web-jsf-client-test.xml");

		PickWareConsumer service = new PickWareConsumer();
		LOGGER.info("得到调用端代理：{jsfMessageConumerService}", service);

		Message message = new Message();
		message.setDestinationCode("sph_reverse_1");
		message.setConnectionSystemId("bd");
		message.setContent("{\"orgId\":6,\"packageCode\":\"W0324318282\",\"pickwareCode\":\"Q184995514\",\"orderId\":10143546269,\"operateTime\":\"2015-09-25 13:24:38\",\"operator\":\"于新明|yuxinming\",\"canReceive\":1,\"operateType\":2}");
		PickWare pickWare = JsonHelper.fromJson(message.getContent(), PickWare.class);
        System.out.printf("111111111111111111111"+pickWare.getOperateTime());
        message.setDestinationType(DestinationType.TOPIC);

		try {
			service.consume(message);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
}
