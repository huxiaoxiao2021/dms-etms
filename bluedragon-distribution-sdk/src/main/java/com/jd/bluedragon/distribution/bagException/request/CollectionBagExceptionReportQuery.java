package com.jd.bluedragon.distribution.bagException.request;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.Date;

/**
 * 集包异常举报查询
 *
 * @author fanggang7
 * @time 2020-09-23 20:42:02 周三
 */
public class CollectionBagExceptionReportQuery extends BasePagerCondition implements Serializable {
    private static final long serialVersionUID = 8821327385424621084L;

    /**
     * 区域code
     */
    private Integer orgCode;

    /**
     * 分拣中心
     */
    private Integer siteCode;

    /**
     * 举报类型
     */
    private Integer reportType;

    /**
     * 举报时间开始
     */
    private String createTimeFromStr;

    private Date createTimeFrom;

    /**
     * 举报时间结束
     */
    private String createTimeToStr;

    private Date createTimeTo;

    private String currentUserErp;

    private Integer pageSize;

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

    public String getCreateTimeFromStr() {
        return createTimeFromStr;
    }

    public void setCreateTimeFromStr(String createTimeFromStr) {
        this.createTimeFromStr = createTimeFromStr;
    }

    public Date getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(Date createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public String getCreateTimeToStr() {
        return createTimeToStr;
    }

    public void setCreateTimeToStr(String createTimeToStr) {
        this.createTimeToStr = createTimeToStr;
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

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "CollectionBagExceptionReportQuery{" +
                "orgCode=" + orgCode +
                ", siteCode=" + siteCode +
                ", reportType=" + reportType +
                ", createTimeFromStr='" + createTimeFromStr + '\'' +
                ", createTimeFrom=" + createTimeFrom +
                ", createTimeToStr='" + createTimeToStr + '\'' +
                ", createTimeTo=" + createTimeTo +
                ", currentUserErp=" + currentUserErp +
                ", pageSize=" + pageSize +
                '}';
    }
}
