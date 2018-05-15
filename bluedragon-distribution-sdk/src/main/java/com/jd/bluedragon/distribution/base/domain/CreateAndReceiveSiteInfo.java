package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;

/**
 * 始发站点和目的站点的基本信息
 * 分拣中心编码
 * 分拣中心名称
 * type
 * subType
 */
public class CreateAndReceiveSiteInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 始发站点code
     */
    private Integer createSiteCode;

    /**
     * 始发站点名称
     */
    private String createSiteName;

    /**
     * 始发站点的type
     */
    private Integer createSiteType;

    /**
     * 始发站点的subType
     */
    private Integer createSiteSubType;


    /**
     * 目的站点的code
     */
    private Integer receiveSiteCode;

    /**
     * 目的站点的名称
     */
    private String receiveSiteName;

    /**
     * 目的站点的type
     */
    private Integer receiveSiteType;

    /**
     * 目的站点的subType
     */
    private Integer receiveSiteSubType;

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

    public Integer getCreateSiteSubType() {
        return createSiteSubType;
    }

    public void setCreateSiteSubType(Integer createSiteSubType) {
        this.createSiteSubType = createSiteSubType;
    }

    public Integer getCreateSiteType() {
        return createSiteType;
    }

    public void setCreateSiteType(Integer createSiteType) {
        this.createSiteType = createSiteType;
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

    public Integer getReceiveSiteSubType() {
        return receiveSiteSubType;
    }

    public void setReceiveSiteSubType(Integer receiveSiteSubType) {
        this.receiveSiteSubType = receiveSiteSubType;
    }

    public Integer getReceiveSiteType() {
        return receiveSiteType;
    }

    public void setReceiveSiteType(Integer receiveSiteType) {
        this.receiveSiteType = receiveSiteType;
    }

    @Override
    public String toString(){
        return "始发站点的信息为：站点id:" + createSiteCode + ";站点名称:" + createSiteName +
                ";type:" + createSiteType + ";subType:" + createSiteSubType +"\n"+
                "目的站点的信息为：站点id:" + receiveSiteCode + ";站点名称:" + receiveSiteName +
                ";type:" + receiveSiteType + ";subType:" + receiveSiteSubType;
    }
}
