package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * Created by xumei3 on 2018/2/26.
 */
public class B2BRouterRequest implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer originalSiteCode;
    private String  originalSiteName;

    private Integer destinationSiteType;
    private Integer destinationSiteCode;
    private String  destinationSiteName;

    private Integer startIndex;
    private Integer endIndex;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
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

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getDestinationSiteType() {
        return destinationSiteType;
    }

    public void setDestinationSiteType(Integer destinationSiteType) {
        this.destinationSiteType = destinationSiteType;
    }
}
