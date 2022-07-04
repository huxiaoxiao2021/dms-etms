package com.jd.bluedragon.common.dto.exceptionReport.expressBill.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.util.Date;

/**
 * @Author: liming522
 * @Description: 面单异常举报 请求对象
 * @Date: create in 2020/12/21 15:02
 */
public class ExpressBillExceptionReportRequest {

    /**
     * 是否是改造后的
     */
    private Boolean isReform = false;

    /**
     *包裹号
     */
    private String packageCode;

    /**
     * 操作人
     */
    private User user;

    /**
     * 异常举报记录对象
     */
    private CurrentOperate currentOperate;

    /**
     * 一级举报类型编码
     */
    private Integer firstReportType;
    /**
     * 一级举报类型名称
     */
    private String firstReportTypeName;

    /**
     * 举报类型（二级）
     */
    private Integer reportType;

    /**
     * 举报类型名称
     */
    private String  reportTypeName;

    /**
     *操作举报时间
     */
    private Date reportTime;

    /**
     * 举报异常图片链接
     */
    private String reportPictureUrls;

    /**
     * 始发站点编码
     */
    private Integer firstSiteCode;

    /**
     * 始发站点名称
     */
    private String firstSiteName;

    /**
     * 备注
     */
    private String remark;

    public Boolean getIsReform() {
        return isReform;
    }

    public void setIsReform(Boolean reform) {
        isReform = reform;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public Integer getFirstReportType() {
        return firstReportType;
    }

    public void setFirstReportType(Integer firstReportType) {
        this.firstReportType = firstReportType;
    }

    public String getFirstReportTypeName() {
        return firstReportTypeName;
    }

    public void setFirstReportTypeName(String firstReportTypeName) {
        this.firstReportTypeName = firstReportTypeName;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public String getReportTypeName() {
        return reportTypeName;
    }

    public void setReportTypeName(String reportTypeName) {
        this.reportTypeName = reportTypeName;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public String getReportPictureUrls() {
        return reportPictureUrls;
    }

    public void setReportPictureUrls(String reportPictureUrls) {
        this.reportPictureUrls = reportPictureUrls;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
    
