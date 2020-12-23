package com.jd.bluedragon.distribution.exceptionReport.billException.request;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/21 18:39
 */
public class ExpressBillExceptionReportQuery extends BasePagerCondition implements Serializable {

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
    private Integer reportOrgCode;

    /**
     * 举报分拣中心
     */
    private Integer reportSiteCode;

    /**
     * 举报类型编码
     */
    private Integer  reportTypeCode;

    private Integer yn;

    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getReportOrgCode() {
        return reportOrgCode;
    }

    public void setReportOrgCode(Integer reportOrgCode) {
        this.reportOrgCode = reportOrgCode;
    }

    public Integer getReportSiteCode() {
        return reportSiteCode;
    }

    public void setReportSiteCode(Integer reportSiteCode) {
        this.reportSiteCode = reportSiteCode;
    }

    public Integer getReportTypeCode() {
        return reportTypeCode;
    }

    public void setReportTypeCode(Integer reportTypeCode) {
        this.reportTypeCode = reportTypeCode;
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
}
    
