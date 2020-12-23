package com.jd.bluedragon.distribution.exceptionReport.billException.domain;

import java.util.Date;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/21 18:38
 */
public class ExpressBillExceptionReport {

    /**
     * 序号
     */
    private Long id;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 区域
     */
    private String orgName;

    /**
     * 区域编码
     */
    private Integer orgCode;

    /**
     * 举报地编码-分拣中心
     */
    private Integer  siteCode;

    /**
     *举报地名称
     */
    private String siteName;


    /**
     * 举报类型名称
     */
    private String reportTypeName;

    /**
     * 举报类型编码
     */
    private Integer reportType;

    /**
     * 举报人
     */
    private String  reportUserErp;

    /**
     * 举报时间
     */
    private Date  reportTime;

    /**
     * 举报照片地址(多个)
     */
    private String reportImgUrls;

    /**
     * 数据库时间
     */
    private Date ts;

    /**
     * 是否逻辑删除：0-已删除，1-已存在  db_column: yn
     */
    private Integer yn;

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

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

    public String getReportTypeName() {
        return reportTypeName;
    }

    public void setReportTypeName(String reportTypeName) {
        this.reportTypeName = reportTypeName;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public String getReportImgUrls() {
        return reportImgUrls;
    }

    public void setReportImgUrls(String reportImgUrls) {
        this.reportImgUrls = reportImgUrls;
    }

    public String getReportUserErp() {
        return reportUserErp;
    }

    public void setReportUserErp(String reportUserErp) {
        this.reportUserErp = reportUserErp;
    }
}
    
