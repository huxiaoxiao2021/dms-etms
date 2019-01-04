package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.reverse.domain.OrderItem;
import com.jd.bluedragon.distribution.reverse.domain.PopMessage;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
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
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    @Qualifier("q20_50MQ")
    private DefaultJMQProducer q20_50MQ;

    @Autowired
    @Qualifier("q20_20MQ")
    private DefaultJMQProducer q20_20MQ;

    @Override
    public boolean sendPopMessage(String waybillCode) {
        try {
            PopMessage popMessage = new PopMessage();
            if (!this.buildAttributes(waybillCode, popMessage)) {
                // 非POP订单
                return true;
            }
            String xmlMessage = popMessage.toXml();
            messageLog.info("逆向回传POP发送的消息体：【 " + xmlMessage + "】,send_key:" + popMessage.getMessageType());
            this.pushMessage(popMessage.getMessageType(), waybillCode, xmlMessage);
            messageLog.info("逆向回传POP发送的消息体：【 " + xmlMessage + "】，发送成功");
            return true;
        } catch (Exception e) {
            log.error("处理失败，运单号：" + waybillCode, e);
            return false;
        }

    }

    /**
     * 构建消息体中的订单商品信息
     *
     * @param waybillCode
     * @param popMessage
     */
    private boolean buildAttributes(String waybillCode, PopMessage popMessage) {
        BaseEntity<BigWaybillDto> baseEntity = this.getWaybillDataByChoice(waybillCode);
        if (baseEntity != null && baseEntity.getData() != null) {
            Waybill waybill = baseEntity.getData().getWaybill();
            if (waybill == null) {
                log.error("【运单号:" + waybillCode + "】未获取到运单对象");
                throw new RuntimeException("【运单号:" + waybillCode + "】未获取到运单对象");
            }
            Integer type = waybill.getWaybillType();
            if (type == null) {
                log.error("【运单号:" + waybillCode + "】运单类型为空");
                throw new RuntimeException("【运单号:" + waybillCode + "】运单类型为空");
            }
            String messageType = this.getMessageTypeByType(type);
            if (StringUtils.isNotEmpty(messageType)) {
                popMessage.setMessageType(messageType);
            } else {
                log.error("【运单号:" + waybillCode + "】是非POP订单");
                return false;
            }

            popMessage.setOrderId(waybillCode);
            popMessage.setUuid(waybillCode);
            popMessage.setBizType(20);
            popMessage.setBusinessTime(new Date());

            this.buildOrderItem(popMessage, baseEntity.getData().getGoodsList());
        } else {
            log.error("【运单号:" + waybillCode + "】调用运单接口返回baseEntity为null");
            throw new RuntimeException("【运单号:" + waybillCode + "】调用运单接口返回baseEntity为null");
        }
        return true;
    }

    /**
     * 调用运单大接口获取运单信息(只包括主运单信息和商品信息)
     *
     * @param waybillCode
     * @return
     */
    private BaseEntity<BigWaybillDto> getWaybillDataByChoice(String waybillCode) {
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(false);
        wChoice.setQueryWaybillM(false);
        wChoice.setQueryPackList(false);
        wChoice.setQueryGoodList(true);
        return waybillQueryManager.getDataByChoice(waybillCode, wChoice);
    }

    private String getMessageTypeByType(Integer type) {
        if (type.equals(21)) {
            //Pop订单类型Fbp 虚入、虚出给商家
            return PopMessage.MESSAGE_TYPE_20_50;
        } else if (type.equals(23) || type.equals(25)) {
            //Pop订单类型(使用京东配送)LBP  虚入、虚出给商家 或者 POP SOPL
            return PopMessage.MESSAGE_TYPE_20_20;
        } else {
            // 非POP订单
            return null;
        }
    }

    private void buildOrderItem(PopMessage popMessage, List<Goods> goods) {
        if (goods != null && goods.size() > 0) {
            for (Goods good : goods) {
                OrderItem orderItem = new OrderItem();
                orderItem.setItemId(good.getSku());
                orderItem.setItemName(StringEscapeUtils.escapeXml(good.getGoodName()));
                orderItem.setItemPrice(good.getGoodPrice());
                orderItem.setItemNum(good.getGoodCount());
                popMessage.addOrderItem(orderItem);
            }
        }
    }

    @Override
    public String sendPopMessageForTest(String waybillCode) {
        StringBuilder result = new StringBuilder();
        try {
            PopMessage popMessage = new PopMessage();
            result.append("begin call back waybill service.\r\n ");
            BaseEntity<BigWaybillDto> baseEntity = this.getWaybillDataByChoice(waybillCode);
            result.append("call back waybill service success.\r\n ");
            if (baseEntity == null || baseEntity.getData() == null) {
                log.error("【" + waybillCode + "】调用运单接口返回baseEntity为null");
                result.append(waybillCode).append("调用运单接口返回baseEntity为null.\r\n");
                throw new RuntimeException("【" + waybillCode + "】调用运单接口返回baseEntity为null");
            }

            Waybill waybill = baseEntity.getData().getWaybill();
            if (waybill == null) {
                log.error("【" + waybillCode + "】未获取到运单对象");
                result.append(waybillCode).append("获取到运单对象为null.\r\n");
                throw new RuntimeException(waybillCode + "未获取到运单对象.");
            }

            Integer type = waybill.getWaybillType();
            if (type == null) {
                log.error("【" + waybillCode + "】号类型为空");
                result.append(waybillCode).append("获取到运单对象的type为null.\r\n");
                throw new RuntimeException("运单类型为空");
            }

            if (type.equals(21)) {
                //Pop订单类型Fbp 虚入、虚出给商家
                result.append("waybill type is 21,it's Fbp.\r\n ");
                popMessage.setMessageType(PopMessage.MESSAGE_TYPE_20_50);
            } else if (type.equals(23)) {
                //Pop订单类型(使用京东配送)LBP  虚入、虚出给商家
                result.append("waybill type is 23,it's LBP.\r\n ");
                popMessage.setMessageType(PopMessage.MESSAGE_TYPE_20_20);
            } else if (type.equals(25)) {
                //POP SOPL
                result.append("waybill type is 25,it's sopl.\r\n ");
                popMessage.setMessageType(PopMessage.MESSAGE_TYPE_20_20);
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
            List<Goods> goods = baseEntity.getData().getGoodsList();
            result.append("call back get products service success.\r\n ");
            result.append(waybillCode).append("获取的商品个数为").append(goods.size()).append(".\r\n");
            this.buildOrderItem(popMessage, baseEntity.getData().getGoodsList());
            result.append("begin translate popMessage to xml.\r\n");
            String xmlMessage = popMessage.toXml();
            result.append("translate popMessage to xml success.\r\n");
            result.append(waybillCode).append("逆向回传POP发送的消息体：【 " + xmlMessage + "】,send_key:" + popMessage.getMessageType() + ".\r\n");
            this.pushMessage(popMessage.getMessageType(), waybillCode, xmlMessage);
            result.append(waybillCode).append("逆向回传POP发送的消息体：【 " + xmlMessage + "】,发送成功\r\n");
            return result.toString();
        } catch (Exception e) {
            result.append(e).append("\r\n");
            log.error("处理失败，运单号：" + waybillCode, e);
            return result.toString();
        }

    }

    @Override
    public String getPopMessageForTest(String waybillCode) {
        StringBuilder result = new StringBuilder();
        try {
            PopMessage popMessage = new PopMessage();
            if (!this.buildAttributes(waybillCode, popMessage)) {
                // 非POP订单
                return "非POP订单";
            }
            String xmlMessage = popMessage.toXml();
            result.append("逆向回传POP发送的消息体：【 ");
            result.append(xmlMessage);
            result.append("】,send_key:");
            result.append(popMessage.getMessageType());
        } catch (Exception e) {
            result.append(e).append("\r\n");
            log.error("处理失败，运单号：" + waybillCode, e);
        }
        return result.toString();
    }

    private final void pushMessage(String popMqType, String businessId, String text) {
        if (PopMessage.MESSAGE_TYPE_20_20.equals(popMqType)) {
            this.q20_20MQ.sendOnFailPersistent(businessId, text);
        }
        if (PopMessage.MESSAGE_TYPE_20_50.equals(popMqType)) {
            this.q20_50MQ.sendOnFailPersistent(businessId, text);
        }
    }

}
