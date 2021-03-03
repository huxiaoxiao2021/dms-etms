package com.jd.bluedragon.distribution.reflowPackage.request;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.Date;

/**
 * 包裹回流扫描报表查询参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-02-28 15:26:36 周日
 */
public class ReflowPackageQuery extends BasePagerCondition implements Serializable {

    private static final long serialVersionUID = 4601213471912475889L;

    /**
     * 场地编号
     */
    private Integer siteCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 创建开始时间
     */
    private String createTimeFromStr;

    /**
     * 创建结束时间
     */
    private String createTimeToStr;

    private Date createTimeFrom;

    private Date createTimeTo;

    /**
     * 创建开始时间
     */
    private String scanTimeFromStr;

    /**
     * 创建结束时间
     */
    private String scanTimeToStr;

    private Date scanTimeFrom;

    private Date scanTimeTo;

    private String currentUserErp;

    private Integer pageSize;

    /**
     * 是否异步导出
     */
    private Integer isAsyncExport;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getCreateTimeFromStr() {
        return createTimeFromStr;
    }

    public void setCreateTimeFromStr(String createTimeFromStr) {
        this.createTimeFromStr = createTimeFromStr;
    }

    public String getCreateTimeToStr() {
        return createTimeToStr;
    }

    public void setCreateTimeToStr(String createTimeToStr) {
        this.createTimeToStr = createTimeToStr;
    }

    public Date getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(Date createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public Date getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(Date createTimeTo) {
        this.createTimeTo = createTimeTo;
    }

    public String getCurrentUserErp() {
        return currentUserErp;
    }

    public void setCurrentUserErp(String currentUserErp) {
        this.currentUserErp = currentUserErp;
    }

    public Integer getIsAsyncExport() {
        return isAsyncExport;
    }

    public void setIsAsyncExport(Integer isAsyncExport) {
        this.isAsyncExport = isAsyncExport;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getScanTimeFromStr() {
        return scanTimeFromStr;
    }

    public void setScanTimeFromStr(String scanTimeFromStr) {
        this.scanTimeFromStr = scanTimeFromStr;
    }

    public String getScanTimeToStr() {
        return scanTimeToStr;
    }

    public void setScanTimeToStr(String scanTimeToStr) {
        this.scanTimeToStr = scanTimeToStr;
    }

    public Date getScanTimeFrom() {
        return scanTimeFrom;
    }

    public void setScanTimeFrom(Date scanTimeFrom) {
        this.scanTimeFrom = scanTimeFrom;
    }

    public Date getScanTimeTo() {
        return scanTimeTo;
    }

    public void setScanTimeTo(Date scanTimeTo) {
        this.scanTimeTo = scanTimeTo;
    }

    @Override
    public String toString() {
        return "ReflowPackageQuery{" +
                "siteCode=" + siteCode +
                ", packageCode='" + packageCode + '\'' +
                ", createTimeFromStr='" + createTimeFromStr + '\'' +
                ", createTimeToStr='" + createTimeToStr + '\'' +
                ", createTimeFrom=" + createTimeFrom +
                ", createTimeTo=" + createTimeTo +
                ", scanTimeFromStr='" + scanTimeFromStr + '\'' +
                ", scanTimeToStr='" + scanTimeToStr + '\'' +
                ", scanTimeFrom=" + scanTimeFrom +
                ", scanTimeTo=" + scanTimeTo +
                ", currentUserErp='" + currentUserErp + '\'' +
                ", pageSize=" + pageSize +
                ", isAsyncExport=" + isAsyncExport +
                '}';
    }
}
