package com.jd.bluedragon.external.crossbow.whems.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * <p>
 *     武汉邮政的推送消息请求体
 *
 * @link https://cf.jd.com/pages/viewpage.action?pageId=73481178 字段解释文档
 *
 * @author wuzuxiang
 * @since 2019/12/18
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public class WuHanEMSEntity {

    /*
    功能项目
    00：运单回传
    01：运单路由下发
    02：运单状态下发
    03：京东发送的订单详细信息
     */
    @XmlElement(name = "ActionCode")
    private String actionCode;

    /*
    物流ID：默认为WHEMS
     */
    @XmlElement(name = "ParternCode")
    private String partnerCode;

    /*
    供货方ID：默认为360BUY
     */
    @XmlElement(name = "ProductProviderID")
    private String productProviderID;

    @XmlElement(name = "ValidationData")
    private String validationData;

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getProductProviderID() {
        return productProviderID;
    }

    public void setProductProviderID(String productProviderID) {
        this.productProviderID = productProviderID;
    }

    public String getValidationData() {
        return validationData;
    }

    public void setValidationData(String validationData) {
        this.validationData = validationData;
    }

}
