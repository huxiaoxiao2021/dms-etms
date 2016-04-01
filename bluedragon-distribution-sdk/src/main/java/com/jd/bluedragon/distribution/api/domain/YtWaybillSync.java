package com.jd.bluedragon.distribution.api.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yangbo7 on 2016/3/28.
 */
public class YtWaybillSync implements Serializable {

    private static final long serialVersionUID = -7036481600572083787L;


    /**
     * 主键
     */
    private Long id;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 运单目的地代码(预分拣站点/目的分拣中心站点/自提柜的归属站点)
     */
    private String siteCode;


    /**
     * site_code是归属站点,则当前字段存预分拣站点
     */
    private String originalSiteCode;

    /**
     * 运单生成时间，重复运单号以生成时间最近的为准
     */
    private String createTime;

    /**
     * 运单第一次落库的时间
     */
    private String createTimeLocal;

    /**
     * 运单更新时间，但不会写入表,用于同步中的排序
     */
    private String updateTime;

    /**
     * 0 正常订单、12 取消订单、13 删除订单
     */
    private Integer expCode;

    /**
     * 特殊属性
     */
    private String sendPay;

    /**
     * 接收标识 0：未接收 1：已接收
     */
    private Integer receFlag;

    /**
     * 数据接收时间
     */
    private String receTime;

    /**
     * 数据库时间
     */
    private Date timesTamp;

    /**
     * 0不生效, 1生效
     */
    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getOriginalSiteCode() {
        return originalSiteCode;
    }

    public void setOriginalSiteCode(String originalSiteCode) {
        this.originalSiteCode = originalSiteCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTimeLocal() {
        return createTimeLocal;
    }

    public void setCreateTimeLocal(String createTimeLocal) {
        this.createTimeLocal = createTimeLocal;
    }

    public Integer getExpCode() {
        return expCode;
    }

    public void setExpCode(Integer expCode) {
        this.expCode = expCode;
    }

    public String getSendPay() {
        return sendPay;
    }

    public void setSendPay(String sendPay) {
        this.sendPay = sendPay;
    }

    public Integer getReceFlag() {
        return receFlag;
    }

    public void setReceFlag(Integer receFlag) {
        this.receFlag = receFlag;
    }

    public String getReceTime() {
        return receTime;
    }

    public void setReceTime(String receTime) {
        this.receTime = receTime;
    }

    public Date getTimesTamp() {
        return timesTamp;
    }

    public void setTimesTamp(Date timesTamp) {
        this.timesTamp = timesTamp;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
