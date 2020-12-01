package com.jd.bluedragon.distribution.mixedPackageConfig.domain;

import java.io.Serializable;

/**
 * 集包地
 *
 * @author: hujiping
 * @date: 2020/8/31 18:09
 */
public class MixedSite implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 集包地站点编码
     * */
    private Integer mixedSiteCode;
    /**
     * 集包地站点简称
     * */
    private String mixedSiteShortName;
    /**
     * 集包地站点名称
     * */
    private String mixedSiteName;
    /**
     * 面单集货地
     * */
    private String collectionAddress;

    public Integer getMixedSiteCode() {
        return mixedSiteCode;
    }

    public void setMixedSiteCode(Integer mixedSiteCode) {
        this.mixedSiteCode = mixedSiteCode;
    }

    public String getMixedSiteShortName() {
        return mixedSiteShortName;
    }

    public void setMixedSiteShortName(String mixedSiteShortName) {
        this.mixedSiteShortName = mixedSiteShortName;
    }

    public String getMixedSiteName() {
        return mixedSiteName;
    }

    public void setMixedSiteName(String mixedSiteName) {
        this.mixedSiteName = mixedSiteName;
    }

    public String getCollectionAddress() {
        return collectionAddress;
    }

    public void setCollectionAddress(String collectionAddress) {
        this.collectionAddress = collectionAddress;
    }
}
