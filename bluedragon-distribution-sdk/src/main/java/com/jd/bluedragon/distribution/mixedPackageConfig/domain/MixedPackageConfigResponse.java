package com.jd.bluedragon.distribution.mixedPackageConfig.domain;


import java.io.Serializable;

/**
 * 混装配置类
 * Created by zhangleqi on 2017/8/27
 */
public class MixedPackageConfigResponse implements Serializable{

    private static final long serialVersionUID = -1453531573126040041L;
    /**
     * id
     */
    private Integer id;

    /**

     /**
     * 建包/发货分拣中心编码
     */
    private Integer createSiteCode;


    /**
     * 目的分拣中心编码
     */
    private Integer receiveSiteCode;

    /**
     * 可混装目的地编码
     */
    private Integer mixedSiteCode;
    /**
     * 可混装目的地名称
     */
    private String mixedSiteName;


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

    public Integer getMixedSiteCode() {
        return mixedSiteCode;
    }

    public void setMixedSiteCode(Integer mixedSiteCode) {
        this.mixedSiteCode = mixedSiteCode;
    }

    public String getMixedSiteName() {
        return mixedSiteName;
    }

    public void setMixedSiteName(String mixedSiteName) {
        this.mixedSiteName = mixedSiteName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}


