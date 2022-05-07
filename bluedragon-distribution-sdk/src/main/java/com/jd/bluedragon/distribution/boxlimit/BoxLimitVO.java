package com.jd.bluedragon.distribution.boxlimit;

import java.io.Serializable;

/**
 * 建箱包裹数配置VO
 */
public class BoxLimitVO implements Serializable {
    private Long id;
    /**
     * 机构名称
     */
    private String siteName;
    /**
     * 机构ID
     */
    private Integer siteId;
    /**
     * 建箱包裹上限
     */
    private Integer limitNum;
    /**
     * 操作人erp
     */
    private String operatorErp;
    /**
     * 操作时间
     */
    private String operatingTime;
    /**
     * 操作机构
     */
    private String operatorSiteName;

    /**
     * 配置类型
     */
    private Integer configType;

    /**
     * 箱号类型
     */
    private String boxNumberType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public String getOperatingTime() {
        return operatingTime;
    }

    public void setOperatingTime(String operatingTime) {
        this.operatingTime = operatingTime;
    }

    public String getOperatorSiteName() {
        return operatorSiteName;
    }

    public void setOperatorSiteName(String operatorSiteName) {
        this.operatorSiteName = operatorSiteName;
    }

    public Integer getConfigType() {
        return configType;
    }

    public void setConfigType(Integer configType) {
        this.configType = configType;
    }

    public String getBoxNumberType() {
        return boxNumberType;
    }

    public void setBoxNumberType(String boxNumberType) {
        this.boxNumberType = boxNumberType;
    }
}
