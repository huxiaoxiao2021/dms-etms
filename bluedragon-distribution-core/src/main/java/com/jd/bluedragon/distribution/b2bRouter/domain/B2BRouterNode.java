package com.jd.bluedragon.distribution.b2bRouter.domain;

import java.io.Serializable;

/**
 * Created by xumei3 on 2018/2/26.
 */
public class B2BRouterNode implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer chainId;

    private Integer originalSiteType;
    private Integer originalSiteCode;
    private String originalSiteName;

    private Integer destinationSiteType;
    private Integer destinationSiteCode;
    private String destinationSiteName;

    public Integer getChainId() {
        return chainId;
    }

    public void setChainId(Integer chainId) {
        this.chainId = chainId;
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
}
