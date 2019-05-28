package com.jd.bluedragon.distribution.waybill.domain;

import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;

/**
 * Created by yanghongqiang on 2015/11/30.
 */
public class LabelPrintingResponse extends BasePrintWaybill {

    /**
     *
     */
    private static final long serialVersionUID = 5929188144402426129L;

    public static final Integer CODE_EMPTY_BASE = 410;
    public static final String MESSAGE_EMPTY_BASE = "没有包裹数据的基础信息";

    public static final Integer CODE_EMPTY_WAYBILL = 420;
    public static final String MESSAGE_EMPTY_WAYBILL = "没有运单数据信息";

    public static final Integer CODE_EMPTY_SITE = 430;
    public static final String MESSAGE_EMPTY_SITE = "没有预分拣站点";

    public static final Integer CODE_EMPTY_PARMAETER = 510;
    public static final String MESSAGE_EMPTY_PARMAETER = "参数不正确";

    /**订单号*/
    public String orderCode;



    /**客户订单地址*/
    private String orderAddress;



    /**是否自提柜*/
    private Integer arayacakCabinet;

    /**标签类型 0：有纸化，1：无纸化*/
    private Integer labelType;

    /**包裹价格*/
    private String packagePrice;

    /**货到付款 true*/
    private Boolean codType;

    /** 时效类型 */
    private String timeCategory;

    /** 承诺信息 */
    private String promiseText;

    public LabelPrintingResponse() {
        super();
    }

    public LabelPrintingResponse(String waybillCode) {
        super(waybillCode);
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public Integer getArayacakCabinet() {
        return arayacakCabinet;
    }

    public void setArayacakCabinet(Integer arayacakCabinet) {
        this.arayacakCabinet = arayacakCabinet;
    }


    public Integer getLabelType() {
        return labelType;
    }

    public void setLabelType(Integer labelType) {
        this.labelType = labelType;
    }

    public String getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(String packagePrice) {
        this.packagePrice = packagePrice;
    }

    public Boolean getCodType() {
        return codType;
    }

    public void setCodType(Boolean codType) {
        this.codType = codType;
    }

    public String getTimeCategory() {
        return timeCategory;
    }

    public void setTimeCategory(String timeCategory) {
        this.timeCategory = timeCategory;
    }

    public String getPromiseText() {
        return promiseText;
    }

    public void setPromiseText(String promiseText) {
        this.promiseText = promiseText;
    }
}
