package com.jd.bluedragon.distribution.jy.send;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/6 20:11
 * @Description: 发货波次汇总数据Dto
 */
public class JySendPredictAggsDto implements Serializable {

    private Long id;

    /**
     * 业务唯一id
     */
    private String uid;

    /**
     *场地id
     */
    private Long siteCode;

    /**
     *计划下个场地id
     */
    private Long planNextSiteCode;



    /**
     *产品类型
     */
    private String productType;

    /**
     *未扫数量
     */
    private Integer unScanCount;

    /**
     * 用于区分当前波次和下一波次
     */
    private Integer flag;


    /**
     * 版本号
     */
    private Long version;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public Long getPlanNextSiteCode() {
        return planNextSiteCode;
    }

    public void setPlanNextSiteCode(Long planNextSiteCode) {
        this.planNextSiteCode = planNextSiteCode;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}
