package com.jd.bluedragon.external.crossbow.whems.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *     武汉邮政的返回类
 *
 * @author wuzuxiang
 * @since 2019/12/19
 **/
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class WuHanEMSResponse extends WuHanEMSEntity{

    @XmlElement(name = "PlaintextData")
    private WuHanEMSResultDto plaintextData;

    public WuHanEMSResultDto getPlaintextData() {
        return plaintextData;
    }

    public void setPlaintextData(WuHanEMSResultDto plaintextData) {
        this.plaintextData = plaintextData;
    }
}
