package com.jd.bluedragon.distribution.reverse.domain;

import com.jd.bluedragon.utils.DateHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类描述： POP发送消息
 * <p>
 * 创建者： libin
 * 项目名称： bluedragon-distribution-sdk
 * 创建时间： 2012-12-17 下午4:29:15
 * 版本号： v1.0
 */
public class PopMessage {

    public static final String MESSAGE_TYPE_20_20 = "20_20";

    public static final String MESSAGE_TYPE_20_50 = "20_50";

    public static final String MESSAGE_TYPE_20_80 = "20_80";

    private static String HEAD = "<?xml version=\"1.0\" encoding=\"utf-16\"?>";

    private static String SCHEME1 = "<FbpBusiness xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">";

    private static String END_SCHEME1 = "</FbpBusiness>";

    private static String SCHEME2 = "<LbpBusiness xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">";

    private static String END_SCHEME2 = "</LbpBusiness>";
    /**
     * 订单号
     */
    public String orderId;
    /**
     * 业务类型10:半退,20：全退
     */
    public Integer bizType;
    /**
     * 唯一键
     */
    public String uuid;

    public Date businessTime;
    /**
     * 消息体类型 20.50 20.80 20.20
     */
    public String messageType;
    /**
     * 商品集合
     */
    public List<OrderItem> orderItemList;

    public void addOrderItem(OrderItem orderItem) {
        if (orderItemList == null) {
            orderItemList = new ArrayList<OrderItem>();
        }
        orderItemList.add(orderItem);
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getBusinessTime() {
        return businessTime != null ? (Date) businessTime.clone() : null;
    }

    public void setBusinessTime(Date businessTime) {
        this.businessTime = businessTime != null ? (Date) businessTime.clone() : null;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String toXml() {
        if (null == this.messageType || "".equals(this.messageType)) {
            throw new RuntimeException("消息体类型为空");
        }

        StringBuffer xmlStr = new StringBuffer();
        xmlStr.append(HEAD);
        if (this.messageType.equals(PopMessage.MESSAGE_TYPE_20_50) || this.messageType.equals(PopMessage.MESSAGE_TYPE_20_80)) {
            xmlStr.append(SCHEME1);
        } else if (this.messageType.equals(PopMessage.MESSAGE_TYPE_20_20)) {
            xmlStr.append(SCHEME2);
        } else {
            throw new RuntimeException("不支持消息体类型为" + this.messageType);
        }
        xmlStr.append("<orderId>").append(this.orderId).append("</orderId>");
        xmlStr.append("<bizType>").append(this.bizType).append("</bizType>");
        xmlStr.append("<uuid>").append(this.uuid).append("</uuid>");
        xmlStr.append("<businessTime>").append(DateHelper.formatDateTime(this.businessTime)).append("</businessTime>");
        if (this.orderItemList != null) {
            xmlStr.append("<orderItemList>");
            for (OrderItem orderItem : this.orderItemList) {
                xmlStr.append("<OrderItem>");
                xmlStr.append("<itemId>").append(orderItem.getItemId()).append("</itemId>");
                xmlStr.append("<itemName>").append(this.invalidXmlStrFilter(orderItem.getItemName())).append("</itemName>");
                xmlStr.append("<itemPrice>").append(orderItem.getItemPrice()).append("</itemPrice>");
                xmlStr.append("<itemNum>").append(orderItem.getItemNum()).append("</itemNum>");
                xmlStr.append("</OrderItem>");

            }
            xmlStr.append("</orderItemList>");
        }
        if (this.messageType.equals(PopMessage.MESSAGE_TYPE_20_50) || this.messageType.equals(PopMessage.MESSAGE_TYPE_20_80)) {
            xmlStr.append(END_SCHEME1);
        } else if (this.messageType.equals(PopMessage.MESSAGE_TYPE_20_20)) {
            xmlStr.append(END_SCHEME2);
        }
        return xmlStr.toString();
    }

    /**
     * 过滤xml无效字符
     *
     * @param xmlStr
     * @return
     */
    private String invalidXmlStrFilter(String xmlStr) {
        StringBuilder sb = new StringBuilder();
        for (char c : xmlStr.toCharArray()) {
            if (!(0x00 < c && c < 0x08 || 0x0b < c && c < 0x0c || 0x0e < c && c < 0x1f)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        PopMessage popMessage = new PopMessage();
        popMessage.setBizType(11);
        popMessage.setBusinessTime(new Date());
        popMessage.setOrderId("111111");
        popMessage.setUuid("uuid");
        popMessage.setMessageType(PopMessage.MESSAGE_TYPE_20_20);
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setItemId("item2");
        orderItem2.setItemName("产品2");
        orderItem2.setItemNum(2);
        orderItem2.setItemPrice("200");

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setItemId("item1");
        orderItem1.setItemName("产品1");
        orderItem1.setItemNum(1);
        orderItem1.setItemPrice("100");
        popMessage.addOrderItem(orderItem1);
        popMessage.addOrderItem(orderItem2);
        String xml = popMessage.toXml();
        System.out.println(xml);
    }

}
