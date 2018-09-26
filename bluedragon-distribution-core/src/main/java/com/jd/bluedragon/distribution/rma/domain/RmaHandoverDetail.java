package com.jd.bluedragon.distribution.rma.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * RMA交接清单打印子表-商品明细维度
 * <p>
 * Created by lixin39 on 2018/9/20.
 */
public class RmaHandoverDetail implements Serializable {

    private static final long serialVersionUID = -7800839106682310757L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 外键主表ID
     */
    private Long handoverWaybillId;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 出库单号
     */
    private String outboundOrderCode;

    /**
     * 商品编码
     */
    private String skuCode;

    /**
     * 备件条码
     */
    private String spareCode;

    /**
     * 商品名称
     */
    private String goodName;

    /**
     * 商品数量
     */
    private Integer goodCount;

    /**
     * 异常备注
     */
    private String exceptionRemark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除 0-已删除，1-未删除
     */
    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHandoverWaybillId() {
        return handoverWaybillId;
    }

    public void setHandoverWaybillId(Long handoverWaybillId) {
        this.handoverWaybillId = handoverWaybillId;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getOutboundOrderCode() {
        return outboundOrderCode;
    }

    public void setOutboundOrderCode(String outboundOrderCode) {
        this.outboundOrderCode = outboundOrderCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSpareCode() {
        return spareCode;
    }

    public void setSpareCode(String spareCode) {
        this.spareCode = spareCode;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public Integer getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(Integer goodCount) {
        this.goodCount = goodCount;
    }

    public String getExceptionRemark() {
        return exceptionRemark;
    }

    public void setExceptionRemark(String exceptionRemark) {
        this.exceptionRemark = exceptionRemark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
