package com.jd.bluedragon.distribution.exceptionReport.billException.dto;

import java.io.Serializable;

/**
 * 面单异常举报成功发出mq消息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-06-09 10:40:15 周三
 */
public class ExpressBillExceptionReportMq implements Serializable {

    /**
     * 包裹号
     */
    private String packageCode;
    /**
     * 区域名称
     */
    private String orgName;
    /**
     * 区域编码
     */
    private Integer orgCode;
    /**
     * 站点ID
     */
    private Integer siteCode;
    /**
     * 站点名称
     */
    private String siteName;
    /**
     * 始发站点ID
     */
    private Integer firstSiteCode;
    /**
     * 始发站点名称
     */
    private String firstSiteName;
    /**
     * 举报类型名称
     */
    private String reportTypeName;
    /**
     * 举报类型
     */
    private Integer reportType;
    /**
     * 举报类型分类
     */
    private Integer reportTypeCategory;
    /**
     * 举报类型分类
     */
    private String reportTypeCategoryName;
    /**
     * 举报人ERP
     */
    private String reportUserErp;
    /**
     * 举报时间
     */
    private Long reportTime;
    /**
     * 举报时间格式化
     */
    private String reportTimeFormat;
    /**
     * 举报图片链接
     */
    private String reportImgUrls;
    /**
     * 商家编码
     */
    private String traderCode;
    /**
     * 被举报人用户ID
     */
    private Long reportedUserId;
    /**
     * 被举报人用户ERP
     */
    private String reportedUserErp;
    /**
     * 被举报人用户姓名
     */
    private String reportedUserName;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 条线类型
     */
    private Integer lineType;
    /**
     * 备注
     */
    private String remark;

    /**
     * 来源系统：2-分拣
     */
    private Integer sourceSystem;
    /**
     * 来源单据：2-分拣异常举报
     */
    private Integer sourceBill;
    /**
     * 来源单据号：traderCCode_reportType
     */
    private String sourceBillCode;
    /**
     * 是否紧急，默认值：true
     */
    private Boolean urGent;

    public String getPackageCode() {
        return packageCode;
    }

    public ExpressBillExceptionReportMq setPackageCode(String packageCode) {
        this.packageCode = packageCode;
        return this;
    }

    public String getOrgName() {
        return orgName;
    }

    public ExpressBillExceptionReportMq setOrgName(String orgName) {
        this.orgName = orgName;
        return this;
    }

    public Integer getOrgCode() {
        return orgCode;
    }

    public ExpressBillExceptionReportMq setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
        return this;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public ExpressBillExceptionReportMq setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
        return this;
    }

    public String getSiteName() {
        return siteName;
    }

    public ExpressBillExceptionReportMq setSiteName(String siteName) {
        this.siteName = siteName;
        return this;
    }

    public Integer getFirstSiteCode() {
        return firstSiteCode;
    }

    public ExpressBillExceptionReportMq setFirstSiteCode(Integer firstSiteCode) {
        this.firstSiteCode = firstSiteCode;
        return this;
    }

    public String getFirstSiteName() {
        return firstSiteName;
    }

    public ExpressBillExceptionReportMq setFirstSiteName(String firstSiteName) {
        this.firstSiteName = firstSiteName;
        return this;
    }

    public String getReportTypeName() {
        return reportTypeName;
    }

    public ExpressBillExceptionReportMq setReportTypeName(String reportTypeName) {
        this.reportTypeName = reportTypeName;
        return this;
    }

    public Integer getReportType() {
        return reportType;
    }

    public ExpressBillExceptionReportMq setReportType(Integer reportType) {
        this.reportType = reportType;
        return this;
    }

    public Integer getReportTypeCategory() {
        return reportTypeCategory;
    }

    public ExpressBillExceptionReportMq setReportTypeCategory(Integer reportTypeCategory) {
        this.reportTypeCategory = reportTypeCategory;
        return this;
    }

    public String getReportTypeCategoryName() {
        return reportTypeCategoryName;
    }

    public ExpressBillExceptionReportMq setReportTypeCategoryName(String reportTypeCategoryName) {
        this.reportTypeCategoryName = reportTypeCategoryName;
        return this;
    }

    public String getReportUserErp() {
        return reportUserErp;
    }

    public ExpressBillExceptionReportMq setReportUserErp(String reportUserErp) {
        this.reportUserErp = reportUserErp;
        return this;
    }

    public Long getReportTime() {
        return reportTime;
    }

    public ExpressBillExceptionReportMq setReportTime(Long reportTime) {
        this.reportTime = reportTime;
        return this;
    }

    public String getReportTimeFormat() {
        return reportTimeFormat;
    }

    public ExpressBillExceptionReportMq setReportTimeFormat(String reportTimeFormat) {
        this.reportTimeFormat = reportTimeFormat;
        return this;
    }

    public String getReportImgUrls() {
        return reportImgUrls;
    }

    public ExpressBillExceptionReportMq setReportImgUrls(String reportImgUrls) {
        this.reportImgUrls = reportImgUrls;
        return this;
    }

    public String getTraderCode() {
        return traderCode;
    }

    public ExpressBillExceptionReportMq setTraderCode(String traderCode) {
        this.traderCode = traderCode;
        return this;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public ExpressBillExceptionReportMq setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
        return this;
    }

    public String getReportedUserErp() {
        return reportedUserErp;
    }

    public ExpressBillExceptionReportMq setReportedUserErp(String reportedUserErp) {
        this.reportedUserErp = reportedUserErp;
        return this;
    }

    public String getReportedUserName() {
        return reportedUserName;
    }

    public ExpressBillExceptionReportMq setReportedUserName(String reportedUserName) {
        this.reportedUserName = reportedUserName;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public ExpressBillExceptionReportMq setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public Integer getLineType() {
        return lineType;
    }

    public ExpressBillExceptionReportMq setLineType(Integer lineType) {
        this.lineType = lineType;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public ExpressBillExceptionReportMq setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public Integer getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(Integer sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public Integer getSourceBill() {
        return sourceBill;
    }

    public void setSourceBill(Integer sourceBill) {
        this.sourceBill = sourceBill;
    }

    public String getSourceBillCode() {
        return sourceBillCode;
    }

    public void setSourceBillCode(String sourceBillCode) {
        this.sourceBillCode = sourceBillCode;
    }

    public Boolean getUrGent() {
        return urGent;
    }

    public void setUrGent(Boolean urGent) {
        this.urGent = urGent;
    }
}
