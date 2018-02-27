package com.jd.bluedragon.distribution.b2bRouter.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xumei3 on 2018/2/26.
 */
public class B2BRouter implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer originalSiteType;
    private Integer originalSiteCode;
    private String  originalSiteName;

    private Integer destinationSiteType;
    private Integer destinationSiteCode;
    private String  destinationSiteName;

    private String siteIdFullLine;
    private String siteNameFullLine;

    private Integer routerChainId;

    private String operatorUserErp;
    private String operatorUserName;

    private Date createTime;
    private Date updateTime;

    private Integer yn;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDestinationSiteCode() {
        return destinationSiteCode;
    }

    public void setDestinationSiteCode(Integer destinationSiteCode) {
        this.destinationSiteCode = destinationSiteCode;
    }

    public String getDestinationSiteName() {
        return destinationSiteName;
    }

    public void setDestinationSiteName(String destinationSiteName) {
        this.destinationSiteName = destinationSiteName;
    }

    public Integer getDestinationSiteType() {
        return destinationSiteType;
    }

    public void setDestinationSiteType(Integer destinationSiteType) {
        this.destinationSiteType = destinationSiteType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperatorUserErp() {
        return operatorUserErp;
    }

    public void setOperatorUserErp(String operatorUserErp) {
        this.operatorUserErp = operatorUserErp;
    }

    public String getOperatorUserName() {
        return operatorUserName;
    }

    public void setOperatorUserName(String operatorUserName) {
        this.operatorUserName = operatorUserName;
    }

    public Integer getOriginalSiteCode() {
        return originalSiteCode;
    }

    public void setOriginalSiteCode(Integer originalSiteCode) {
        this.originalSiteCode = originalSiteCode;
    }

    public String getOriginalSiteName() {
        return originalSiteName;
    }

    public void setOriginalSiteName(String originalSiteName) {
        this.originalSiteName = originalSiteName;
    }

    public Integer getOriginalSiteType() {
        return originalSiteType;
    }

    public void setOriginalSiteType(Integer originalSiteType) {
        this.originalSiteType = originalSiteType;
    }

    public Integer getRouterChainId() {
        return routerChainId;
    }

    public void setRouterChainId(Integer routerChainId) {
        this.routerChainId = routerChainId;
    }

    public String getSiteIdFullLine() {
        return siteIdFullLine;
    }

    public void setSiteIdFullLine(String siteIdFullLine) {
        this.siteIdFullLine = siteIdFullLine;
    }

    public String getSiteNameFullLine() {
        return siteNameFullLine;
    }

    public void setSiteNameFullLine(String siteNameFullLine) {
        this.siteNameFullLine = siteNameFullLine;
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
