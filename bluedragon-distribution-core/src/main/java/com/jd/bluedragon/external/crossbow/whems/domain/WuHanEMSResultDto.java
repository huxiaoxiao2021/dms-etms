package com.jd.bluedragon.external.crossbow.whems.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *      武汉邮政对象实体，不要问这个javabean存在的意义，呵
 * @author wuzuxiang
 * @since 2019/12/19
 **/
@XmlRootElement(name = "PlaintextData")
@XmlAccessorType(XmlAccessType.FIELD)
public class WuHanEMSResultDto {

    /**
     * 000 :交易成功
     * 001：验证失败
     * 002：数据接受失败，附带每个订单的失败原因
     * 003：没有可接受数据
     */
    @XmlElement(name = "ResultCode")
    private String resultCode;

    /**
     * 0000:订单不存在
     * 0001：订单不属于贵公司
     * 0002：订单已经配送成功，无法操作
     * 0003：内部处理异常
     * 0004：运单号已经存在
     * 0005：增加订单运单关联失败
     */
    @XmlElement(name = "ResultText")
    private String resultText;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }
}
