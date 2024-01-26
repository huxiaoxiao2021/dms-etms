package com.jd.bluedragon.distribution.box.domain;

import java.io.Serializable;
import java.util.List;

public class StoreBoxDetail implements Serializable {
    private static final long serialVersionUID = 7140159170176745255L;


    /**
     * 箱号
     */
    private String boxCode;
    /**
     * 始发场地id （仓忽略）
     */
    private Integer createSiteCode;
    private String createSiteName;
    /**
     * 仓信息
     */
    private StoreInfo storeInfo;
    /**
     *  目的场地id
     */
    private Integer receiveSiteCode;
    /** 接收站点名称 */
    private String receiveSiteName;

    private List<BoxPackageDto> packageList;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public StoreInfo getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(StoreInfo storeInfo) {
        this.storeInfo = storeInfo;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public List<BoxPackageDto> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<BoxPackageDto> packageList) {
        this.packageList = packageList;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }
}
