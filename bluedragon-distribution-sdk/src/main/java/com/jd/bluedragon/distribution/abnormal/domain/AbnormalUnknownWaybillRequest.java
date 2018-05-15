package com.jd.bluedragon.distribution.abnormal.domain;

/**
 * @author tangchunqing
 * @Description: MQ 请求对象
 * @date 2018年05月12日 13时:35分
 */
public class AbnormalUnknownWaybillRequest {
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 上报次数
     */
    private Integer reportNumber;

    /**
     * 商家id
     *
     * @return
     */
    private Integer traderId;

    /**
     * 商品名称
     *
     * @return
     */
    private String traderName;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getReportNumber() {
        return reportNumber;
    }

    public void setReportNumber(Integer reportNumber) {
        this.reportNumber = reportNumber;
    }

    public Integer getTraderId() {
        return traderId;
    }

    public void setTraderId(Integer traderId) {
        this.traderId = traderId;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }
}
