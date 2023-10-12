package com.jd.bluedragon.common.dto.collectpackage.response;

/**
 * @author liwenji
 * @description 流向信息
 * @date 2023-10-12 17:56
 */
public class SiteInfo {

    private Long endSiteId;

    private String endSiteName;

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }
}
