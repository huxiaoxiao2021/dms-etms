package com.jd.bluedragon.distribution.boxlimit;

import java.io.Serializable;

/**
 * 建箱包裹数配置 查询参数
 */
public class BoxLimitQueryDTO implements Serializable {
    /**
     * 机构名称
     */
    private String siteName;
    /**
     * 机构ID
     */
    private Integer siteId;

    private Integer pageNum;

    private Integer pageSize;

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

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
