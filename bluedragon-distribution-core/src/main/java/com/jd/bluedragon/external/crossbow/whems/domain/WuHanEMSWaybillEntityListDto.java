package com.jd.bluedragon.external.crossbow.whems.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * <p>
 *     运单信息列表
 *
 * @author wuzuxiang
 * @since 2019/12/18
 **/
@XmlRootElement(name = "OrderShipList")
@XmlAccessorType(XmlAccessType.FIELD)
public class WuHanEMSWaybillEntityListDto {

    @XmlElement(name = "OrderShip")
    private List<WuHanEMSWaybillEntityDto> orderShipList;

    public List<WuHanEMSWaybillEntityDto> getOrderShipList() {
        return orderShipList;
    }

    public void setOrderShipList(List<WuHanEMSWaybillEntityDto> orderShipList) {
        this.orderShipList = orderShipList;
    }
}
