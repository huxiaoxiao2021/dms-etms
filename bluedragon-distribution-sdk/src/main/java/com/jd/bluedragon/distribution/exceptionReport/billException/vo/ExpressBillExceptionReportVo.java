package com.jd.bluedragon.distribution.exceptionReport.billException.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/22 15:36
 * @date 2020-12-28 14:09
 * @author liming522
 */
public class ExpressBillExceptionReportVo implements Serializable {
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
     * 始发站点编码
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
    private Date reportTime;

    /**
     * 举报时间格式化
     */
    private String reportTimeFormat;

    /**
     * 举报照片地址(多个)
     */
    private String reportImgUrls;

    /**
     * 数据库时间
     */
    private Date ts;

    /**
     * 图片地址集合
     */
    private List<String> reportImgUrlList;

    /**
     * 商家编码
     */
    private String traderCode;
    /**
     * 被举报人ID
     */
    private Long reportedUserId;

    /**
     * 被举报人erp
     */
    private String reportedUserErp;

    /**
     * 被举报人姓名
     */
    private String reportedUserName;

    /**
     * 是否逻辑删除：0-已删除，1-已存在  db_column: yn
     */
    private Boolean yn;

    /**
     * 举报备注
     */
    private String remark;

    /**
     * 商城订单号
     */
    private String orderId;

    /**
     * 商家名称
     */
    private String traderName;
    /**
     * 商家所属大区编码
     */
    private Integer traderOrgCode;
    /**
     * 商家所属大区名称
     */
    private String traderOrgName;

    /**
     * 销售人员ERP
     */
    private String saleStaffErp;
    /**
     * 工单状态
     */
    private String orderStatus;
    /**
     * 驳回原因
     */
    private String rejectedReason;

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
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

    public void setReportImgUrlList(List<String> reportImgUrlList) {
        this.reportImgUrlList = reportImgUrlList;
    }

    public List<String> getReportImgUrlList() {
        return reportImgUrlList;
    }

    public String getReportUserErp() {
        return reportUserErp;
    }

    public void setReportUserErp(String reportUserErp) {
        this.reportUserErp = reportUserErp;
    }

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

    public String getReportTimeFormat() {
        return reportTimeFormat;
    }

    public void setReportTimeFormat(String reportTimeFormat) {
        this.reportTimeFormat = reportTimeFormat;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTraderCode() {
        return traderCode;
    }

    public ExpressBillExceptionReportVo setTraderCode(String traderCode) {
        this.traderCode = traderCode;
        return this;
    }


    public Long getReportedUserId() {
        return reportedUserId;
    }

    public ExpressBillExceptionReportVo setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
        return this;
    }

    public String getReportedUserErp() {
        return reportedUserErp;
    }

    public ExpressBillExceptionReportVo setReportedUserErp(String reportedUserErp) {
        this.reportedUserErp = reportedUserErp;
        return this;
    }

    public String getReportedUserName() {
        return reportedUserName;
    }

    public ExpressBillExceptionReportVo setReportedUserName(String reportedUserName) {
        this.reportedUserName = reportedUserName;
        return this;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public Integer getTraderOrgCode() {
        return traderOrgCode;
    }

    public void setTraderOrgCode(Integer traderOrgCode) {
        this.traderOrgCode = traderOrgCode;
    }

    public String getTraderOrgName() {
        return traderOrgName;
    }

    public void setTraderOrgName(String traderOrgName) {
        this.traderOrgName = traderOrgName;
    }

    public String getSaleStaffErp() {
        return saleStaffErp;
    }

    public void setSaleStaffErp(String saleStaffErp) {
        this.saleStaffErp = saleStaffErp;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }
}
    
