package com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse;

import java.io.Serializable;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/28 21:11
 */
public class FirstSiteVo implements Serializable {
    /**
     * 始发站点编码
     */
    private Integer firstSiteCode;

    /**
     * 始发站点名称
     */
    private String  firstSiteName;

    /**
     * 被举报人ID
     */
    private Long reportedId;

    /**
     * 被举报人ERP
     */
    private String reportedErp;

    /**
     * 被举报人姓名
     */
    private String reportedName;

    public Integer getFirstSiteCode() {
        return firstSiteCode;
    }

    public void setFirstSiteCode(Integer firstSiteCode) {
        this.firstSiteCode = firstSiteCode;
    }

    public String getFirstSiteName() {
        return firstSiteName;
    }

    public void setFirstSiteName(String firstSiteName) {
        this.firstSiteName = firstSiteName;
    }

    public Long getReportedId() {
        return reportedId;
    }

    public FirstSiteVo setReportedId(Long reportedId) {
        this.reportedId = reportedId;
        return this;
    }

    public String getReportedErp() {
        return reportedErp;
    }

    public FirstSiteVo setReportedErp(String reportedErp) {
        this.reportedErp = reportedErp;
        return this;
    }

    public String getReportedName() {
        return reportedName;
    }

    public FirstSiteVo setReportedName(String reportedName) {
        this.reportedName = reportedName;
        return this;
    }
}
    
