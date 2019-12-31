package com.jd.bluedragon.external.crossbow.whems.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *     武汉邮政对象实体，不要问这个javabean存在的意义，呵
 *
 * @author wuzuxiang
 * @since 2019/12/19
 **/
@XmlRootElement(name = "PlaintextData")
@XmlAccessorType(XmlAccessType.FIELD)
public class WuHanEMSWaybillEntityOrderDto {

    @XmlElement(name = "OrderShipList")
    private WuHanEMSWaybillEntityListDto orderShipList;

    public WuHanEMSWaybillEntityListDto getOrderShipList() {
        return orderShipList;
    }

    public void setOrderShipList(WuHanEMSWaybillEntityListDto orderShipList) {
        this.orderShipList = orderShipList;
    }
}
