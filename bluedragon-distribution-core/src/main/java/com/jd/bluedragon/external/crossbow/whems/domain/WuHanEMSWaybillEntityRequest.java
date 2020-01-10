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
 * @since 2019/12/18
 **/
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class WuHanEMSWaybillEntityRequest extends WuHanEMSEntity {

    @XmlElement(name = "PlaintextData")
    private WuHanEMSWaybillEntityOrderDto plaintextData;

    public WuHanEMSWaybillEntityOrderDto getPlaintextData() {
        return plaintextData;
    }

    public void setPlaintextData(WuHanEMSWaybillEntityOrderDto plaintextData) {
        this.plaintextData = plaintextData;
    }
}
