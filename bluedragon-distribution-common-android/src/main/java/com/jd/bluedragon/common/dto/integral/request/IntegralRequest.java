package com.jd.bluedragon.common.dto.integral.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/20
 */
public class IntegralRequest extends BaseReq implements Serializable {

    // 查询日期
    private Date queryDate;

    // 查询开始日期
    private Date startDate;

    // 查询结束日期
    private Date endDate;

    // 大区信息
    private Integer orgCode;

    // 站点信息
    private Long siteCode;

    // 指标
    private String quotaNo;

    public Date getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(Date queryDate) {
        this.queryDate = queryDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public String getQuotaNo() {
        return quotaNo;
    }

    public void setQuotaNo(String quotaNo) {
        this.quotaNo = quotaNo;
    }
}
