package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * Created by xumei3 on 2018/2/26.
 */
public class B2BRouterRequest implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     *始发网点类型
     */
    private Integer originalSiteType;

    /**
     * 始发网点编码
     */
    private Integer originalSiteCode;

    /**
     * 始发网点名称
     */
    private String  originalSiteName;

    /**
     * 目的网点类型
     */
    private Integer destinationSiteType;

    /**
     * 目的网点编码
     */
    private Integer destinationSiteCode;

    /**
     * 目的网点名称
     */
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

    public Integer getOriginalSiteType() {
        return originalSiteType;
    }

    public void setOriginalSiteType(Integer originalSiteType) {
        this.originalSiteType = originalSiteType;
    }
}
