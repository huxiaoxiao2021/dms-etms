package com.jd.bluedragon.distribution.exceptionReport.billException.request;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/21 18:39
 */
public class ExpressBillExceptionReportQuery  implements Serializable {

    private static final long serialVersionUID = -2424172770599768238L;
    /**
     * 查询起始时间
     */
    private String  queryStartTimeStr;

    private Date queryStartTime;

    /**
     * 查询截至时间
     */
    private String queryEndTimeStr;

    private Date queryEndTime;

    /**
     * 举报区域
     */
    private Integer orgCode;

    /**
     * 举报分拣中心
     */
    private Integer siteCode;

    /**
     * 举报类型编码
     */
    private Integer  reportType;

    private Integer yn;

    private Integer pageSize;

    private BasePagerCondition basePagerCondition;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getQueryStartTimeStr() {
        return queryStartTimeStr;
    }

    public void setQueryStartTimeStr(String queryStartTimeStr) {
        this.queryStartTimeStr = queryStartTimeStr;
    }

    public String getQueryEndTimeStr() {
        return queryEndTimeStr;
    }

    public void setQueryEndTimeStr(String queryEndTimeStr) {
        this.queryEndTimeStr = queryEndTimeStr;
    }

    public Date getQueryStartTime() {
        return queryStartTime;
    }

    public void setQueryStartTime(Date queryStartTime) {
        this.queryStartTime = queryStartTime;
    }

    public Date getQueryEndTime() {
        return queryEndTime;
    }

    public void setQueryEndTime(Date queryEndTime) {
        this.queryEndTime = queryEndTime;
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

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public BasePagerCondition getBasePagerCondition() {
        return basePagerCondition;
    }

    public void setBasePagerCondition(BasePagerCondition basePagerCondition) {
        this.basePagerCondition = basePagerCondition;
    }
}
    
