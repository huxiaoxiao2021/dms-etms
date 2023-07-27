package com.jd.bluedragon.distribution.jy.send;

import java.io.Serializable;
import java.util.Date;

/**
 * 拣运发货波次待扫数据PO
 */
public class JySendPredictAggsPO implements Serializable {

    private Long id;

    /**
     * 业务唯一id
     */
    private String uId;

    /**
     *场地id
     */
    private Long siteId;

    /**
     *计划下个场地id
     */
    private Long planNextSiteId;



    /**
     *产品类型
     */
    private String productType;

    /**
     *未扫数量
     */
    private Integer unScanCount;

    private Integer flag;

    private Date createTime;

    private Date updateTime;

    private Integer yn;

    private Date ts;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getPlanNextSiteId() {
        return planNextSiteId;
    }

    public void setPlanNextSiteId(Long planNextSiteId) {
        this.planNextSiteId = planNextSiteId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getUnScanCount() {
        return unScanCount;
    }

    public void setUnScanCount(Integer unScanCount) {
        this.unScanCount = unScanCount;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
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

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}