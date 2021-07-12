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
    private Long reportedUserId;

    /**
     * 被举报人ERP
     */
    private String reportedUserErp;

    /**
     * 被举报人姓名
     */
    private String reportedUserName;

    /**
     * 仓配或终端条线类型
     */
    private Integer lineType;

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

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public FirstSiteVo setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
        return this;
    }

    public String getReportedUserErp() {
        return reportedUserErp;
    }

    public FirstSiteVo setReportedUserErp(String reportedUserErp) {
        this.reportedUserErp = reportedUserErp;
        return this;
    }

    public String getReportedUserName() {
        return reportedUserName;
    }

    public FirstSiteVo setReportedUserName(String reportedUserName) {
        this.reportedUserName = reportedUserName;
        return this;
    }

    public Integer getLineType() {
        return lineType;
    }

    public FirstSiteVo setLineType(Integer lineType) {
        this.lineType = lineType;
        return this;
    }
}
    
