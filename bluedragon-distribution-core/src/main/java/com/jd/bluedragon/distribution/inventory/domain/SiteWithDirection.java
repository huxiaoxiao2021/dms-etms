package com.jd.bluedragon.distribution.inventory.domain;

public class SiteWithDirection {
    /**
     * 站点编码
     */
    private Integer siteCode;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 站点类型
     */
    private Integer siteType;

    /**
     * 站点子类型
     */
    private Integer siteSubType;

    /**
     * 流向编码
     */
    private Integer directionCode;

    /**
     * 流向名称
     */
    private String directionName;

    public static Integer DIRECTION_CODE_TERMINAL_SITE = -1;
    public static Integer DIRECTION_CODE_CONVEY = -2;

    public static String DIRECTION_NAME_TERMINAL_SITE = "终端";
    public static String DIRECTION_NAME_CONVEY = "城配车队";


    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Integer getSiteSubType() {
        return siteSubType;
    }

    public void setSiteSubType(Integer siteSubType) {
        this.siteSubType = siteSubType;
    }

    public Integer getDirectionCode() {
        return directionCode;
    }

    public void setDirectionCode(Integer directionCode) {
        this.directionCode = directionCode;
    }

    public String getDirectionName() {
        return directionName;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }
}
