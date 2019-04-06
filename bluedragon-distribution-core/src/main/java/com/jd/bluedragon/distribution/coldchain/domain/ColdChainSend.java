package com.jd.bluedragon.distribution.coldchain.domain;

import java.util.Date;

/**
 * @author lixin39
 * @Description 冷链发货信息表
 * @ClassName ColdChainSend
 * @date 2019/3/28
 */
public class ColdChainSend {

    /**
     * ID主键
     */
    private Long id;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 运输计划编码
     */
    private String transPlanCode;

    /**
     * 始发站点编号
     */
    private Integer createSiteCode;

    /**
     * 目的站点编号
     */
    private Integer receiveSiteCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

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

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getTransPlanCode() {
        return transPlanCode;
    }

    public void setTransPlanCode(String transPlanCode) {
        this.transPlanCode = transPlanCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
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
}
