package com.jd.bluedragon.distribution.boxlimit;

import java.io.Serializable;

/**
 * 建箱包裹数配置
 */
public class BoxLimitDTO implements Serializable {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }
}
