package com.jd.bluedragon.distribution.jy.exception;

import java.io.Serializable;

/**
 * 异常通知客服MQ实体
 *
 * @author hujiping
 * @date 2023/3/16 10:23 AM
 */
public class JyExNoticeCustomerMQ implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务唯一标识
     */
    private String businessId;

    /**
     * 异常类型
     */
    private Integer exceptionType;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 提报人ERP
     */
    private String handlerErp;

    /**
     * 提报场地ID
     */
    private Integer siteCode;

    /**
     * 提报场地名称
     */
    private String siteName;

    /**
     * 提报时间戳
     */
    private Long handlerTime;

    /**
     * 物品图片
     */
    private String goodsImageUrl;

    /**
     * 证明图片
     */
    private String certifyImageUrl;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Integer getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(Integer exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getHandlerErp() {
        return handlerErp;
    }

    public void setHandlerErp(String handlerErp) {
        this.handlerErp = handlerErp;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Long getHandlerTime() {
        return handlerTime;
    }

    public void setHandlerTime(Long handlerTime) {
        this.handlerTime = handlerTime;
    }

    public String getGoodsImageUrl() {
        return goodsImageUrl;
    }

    public void setGoodsImageUrl(String goodsImageUrl) {
        this.goodsImageUrl = goodsImageUrl;
    }

    public String getCertifyImageUrl() {
        return certifyImageUrl;
    }

    public void setCertifyImageUrl(String certifyImageUrl) {
        this.certifyImageUrl = certifyImageUrl;
    }
}
