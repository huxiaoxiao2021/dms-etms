package com.jd.bluedragon.distribution.cacheClean.domain;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigInteger;

import java.sql.Time;
import java.util.Date;

/**
 * Created by zhoutao on 2017/6/13.
 */
public class CacheClean implements Serializable {



    private static final long serialVersionUID = 7402935664670381058L;

    private String boxCode;

    private String boxType;

    private String mixBoxTypeText;

    private Integer createSiteCode;

    private String createSiteName;

    private String categoryText;

    private Integer receiveSiteCode;

    private String receiveSiteName;

    private String router;

    private String createTime;

    private String updateTime;

    private BigInteger boxId;

    private Integer yn;

    private String batchId;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getBoxType() {
        return boxType;
    }

    public void setBoxType(String boxType) {
        this.boxType = boxType;
    }

    public String getMixBoxTypeText() {
        return mixBoxTypeText;
    }

    public void setMixBoxTypeText(String mixBoxTypeText) {
        this.mixBoxTypeText = mixBoxTypeText;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getCategoryText() {
        return categoryText;
    }

    public void setCategoryText(String categoryText) {
        this.categoryText = categoryText;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public BigInteger getBoxId() {
        return boxId;
    }

    public void setBoxId(BigInteger boxId) {
        this.boxId = boxId;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}