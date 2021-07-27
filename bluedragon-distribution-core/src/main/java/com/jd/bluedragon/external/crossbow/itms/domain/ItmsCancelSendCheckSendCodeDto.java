package com.jd.bluedragon.external.crossbow.itms.domain;

import java.io.Serializable;

/**
 * @ClassName ItmsSendCheckSendCodeDto
 * @Description 发货批次校验请求参数
 * @Author wyh
 * @Date 2021/6/8 10:16
 **/
public class ItmsCancelSendCheckSendCodeDto implements Serializable {

    private static final long serialVersionUID = 7573504907479971410L;

    /**
     * 租户编码
     */
    private String partnerNo;

    /**
     * 交接单号
     */
    private String receiptCode;

    /**
     * 目的网点
     */
    private String opeSiteId;

    public String getPartnerNo() {
        return partnerNo;
    }

    public void setPartnerNo(String partnerNo) {
        this.partnerNo = partnerNo;
    }

    public String getReceiptCode() {
        return receiptCode;
    }

    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode;
    }

    public String getOpeSiteId() {
        return opeSiteId;
    }

    public void setOpeSiteId(String opeSiteId) {
        this.opeSiteId = opeSiteId;
    }
}
