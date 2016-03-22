package com.jd.bluedragon.distribution.reverse.service;

import java.util.Date;
import java.util.List;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.jmq.common.exception.JMQException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.reverse.domain.OrderItem;
import com.jd.bluedragon.distribution.reverse.domain.PopMessage;

/**
 * 
* 类描述： 给POP系统发送消息
* 创建者： libin
* 项目名称： bluedragon-distribution-core
* 创建时间： 2012-12-17 下午5:47:27
* 版本号： v1.0
 */
@Service
public class ReverseSendPopMessageServiceImpl implements ReverseSendPopMessageService {
	private static Log log = LogFactory.getLog(ReverseSendPopMessageServiceImpl.class);
	private static Log messageLog = LogFactory.getLog("com.jd.etms.message");
	@Autowired
	private WaybillCommonService waybillCommonService;
	@Autowired
	private ProductService productService;

	private String SEND_KEY1 = "20_20";
	private String SEND_KEY2 = "20_50";
	//private String sendKey;

    @Autowired
    @Qualifier("q20_50MQ")
    private DefaultJMQProducer q20_50MQ;

    @Autowired
    @Qualifier("q20_20MQ")
    private DefaultJMQProducer q20_20MQ;


	public boolean sendPopMessage(String waybillCode) {
        String sendKey;
		try {
			Waybill waybill = waybillCommonService.findByWaybillCode(waybillCode);
			if (waybill == null) {
				log.error("【" + waybillCode + "】未获取到运单对象");
				throw new RuntimeException(waybillCode + "未获取到运单对象");
			}
			Integer type = waybill.getType();
			if (type == null) {
				log.error("【" + waybillCode + "】号类型为空");
				throw new RuntimeException("运单类型为空");
			}
			PopMessage popMessage = new PopMessage();
			if (type.equals(21)) {//Pop订单类型Fbp 虚入、虚出给商家
				popMessage.setMessageType(PopMessage.MESSAGE_TYPE_20_50);
				sendKey = this.SEND_KEY2;
			} else if (type.equals(23)) {//Pop订单类型(使用京东配送)LBP  虚入、虚出给商家
				popMessage.setMessageType(PopMessage.MESSAGE_TYPE_20_20);
				sendKey = this.SEND_KEY1;
			} else if (type.equals(25)) {//POP SOPL 
				popMessage.setMessageType(PopMessage.MESSAGE_TYPE_20_20);
				sendKey = this.SEND_KEY1;
			} else {
				log.error("【" + waybillCode + "】号是非POP订单");
				return true;
			}
			popMessage.setOrderId(waybillCode);
			popMessage.setUuid(waybillCode);
			popMessage.setBizType(20);
			popMessage.setBusinessTime(new Date());

			List<Product> productList = productService.getOrderProducts(Long.parseLong(waybillCode));
			if (productList != null && productList.size() > 0) {
				for (Product product : productList) {
					OrderItem orderItem = new OrderItem();
					orderItem.setItemId(product.getProductId());
					orderItem.setItemName(StringEscapeUtils.escapeXml(product.getName()));
					orderItem.setItemPrice(product.getPrice().toString());
					orderItem.setItemNum(product.getQuantity());
					popMessage.addOrderItem(orderItem);
				}
			}

			String xmlMessage = popMessage.toXml();
			messageLog.info("逆向回传POP发送的消息体：【 " + xmlMessage + "】,send_key:" + sendKey);
			//messageClient.sendMessage(sendKey, xmlMessage, waybillCode);
            pushMessage(sendKey,waybillCode,xmlMessage);
			messageLog.info("逆向回传POP发送的消息体：【 " + xmlMessage + "】，发送成功");
			return true;
		} catch (Exception e) {
			log.error("处理失败，运单号：" + waybillCode,e);
			return false;
		}

	}
	
	
	public String sendPopMessageForTest(String waybillCode) {
        String sendKey;
		StringBuilder result = new StringBuilder();
		try {
			result.append("begin call back waybill service.\r\n ");
			Waybill waybill = waybillCommonService.findByWaybillCode(waybillCode);
			result.append("call back waybill service success.\r\n ");
			if (waybill == null) {
				log.error("【" + waybillCode + "】未获取到运单对象");
				result.append(waybillCode).append("获取到运单对象为null.\r\n");
				throw new RuntimeException(waybillCode + "未获取到运单对象.");
			}
			Integer type = waybill.getType();
			if (type == null) {
				log.error("【" + waybillCode + "】号类型为空");
				result.append(waybillCode).append("获取到运单对象的type为null.\r\n");
				throw new RuntimeException("运单类型为空");
			}
			PopMessage popMessage = new PopMessage();
			if (type.equals(21)) {//Pop订单类型Fbp 虚入、虚出给商家
				result.append("waybill type is 21,it's Fbp.\r\n ");
				popMessage.setMessageType(PopMessage.MESSAGE_TYPE_20_50);
				sendKey = this.SEND_KEY2;
			} else if (type.equals(23)) {//Pop订单类型(使用京东配送)LBP  虚入、虚出给商家
				result.append("waybill type is 23,it's LBP.\r\n ");
				popMessage.setMessageType(PopMessage.MESSAGE_TYPE_20_20);
				sendKey = this.SEND_KEY1;
			} else if (type.equals(25)) {//POP SOPL 
				result.append("waybill type is 25,it's sopl.\r\n ");
				popMessage.setMessageType(PopMessage.MESSAGE_TYPE_20_20);
				sendKey = this.SEND_KEY1;
			} else {
				log.error("【" + waybillCode + "】号是非POP订单");
				result.append(waybillCode).append("号是非POP订单,订单类型为").append(type).append(".");
				return result.toString();
			}
			popMessage.setOrderId(waybillCode);
			popMessage.setUuid(waybillCode);
			popMessage.setBizType(20);
			popMessage.setBusinessTime(new Date());
			result.append("begin call back get products service.\r\n ");
			List<Product> productList = productService.getOrderProducts(Long.parseLong(waybillCode));
			result.append("call back get products service success.\r\n ");
			result.append(waybillCode).append("获取的商品个数为").append(productList.size()).append(".\r\n");
			if (productList != null && productList.size() > 0) {
				for (Product product : productList) {
					OrderItem orderItem = new OrderItem();
					orderItem.setItemId(product.getProductId());
					orderItem.setItemName(StringEscapeUtils.escapeXml(product.getName()));
					orderItem.setItemPrice(product.getPrice().toString());
					orderItem.setItemNum(product.getQuantity());
					popMessage.addOrderItem(orderItem);
				}
			}
			result.append("begin translate popMessage to xml.\r\n");
			String xmlMessage = popMessage.toXml();
			result.append("translate popMessage to xml success.\r\n");
			result.append(waybillCode).append("逆向回传POP发送的消息体：【 " + xmlMessage + "】,send_key:" + sendKey+".\r\n");
			//messageClient.sendMessage(sendKey, xmlMessage, waybillCode);
            pushMessage(sendKey,waybillCode,xmlMessage);
		    result.append(waybillCode).append("逆向回传POP发送的消息体：【 " + xmlMessage + "】,发送成功\r\n");
			return result.toString();
		} catch (Exception e) {
			result.append(e).append("\r\n");
			log.error("处理失败，运单号：" + waybillCode,e);
			return result.toString();
		}

	}

    private final void pushMessage(String popMqType,String businessId,String text){
        if(this.SEND_KEY1.equals(popMqType)){
            try {
                this.q20_20MQ.send(businessId,text);
            } catch (JMQException e) {
                //wangtingweiDEBUGe.printStackTrace();
            }
        }
        if(this.SEND_KEY2.equals(popMqType)){
            try {
                this.q20_50MQ.send(businessId,text);
            } catch (JMQException e) {
                //wangtingweiDEBUG
            }
        }
    }
	

}
