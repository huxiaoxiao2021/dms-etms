package com.jd.bluedragon.distribution.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PopQueueQuery {
	public String queueNo;
	public String createSiteCode;
	public String expressCode;
	public String expressName;
	public Integer createUserCode;
	public String createUser;
	public Date operateTime;
	public Integer queueStatus;
	public String startTime;
	public String endTime;
	public String printStartTime;
	public String printEndTime;
	public Integer orgCode;

	
	public Integer start;
	public Integer end;
    /**
     * 每页记录条数
     */
    private Integer pageSize;

	/**
	 * 省区编码
	 */
	private String provinceAgencyCode;
	/**
	 * 枢纽编码
	 */
	private String areaHubCode;

	public String getQueueNo() {
		return queueNo;
	}
	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}
	public String getCreateSiteCode() {
		return createSiteCode;
	}
	public void setCreateSiteCode(String createSiteCode) {
		this.createSiteCode = createSiteCode;
	}
	public String getExpressCode() {
		return expressCode;
	}
	public void setExpressCode(String expressCode) {
		this.expressCode = expressCode;
	}
	public String getExpressName() {
		return expressName;
	}
	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}
	public Integer getCreateUserCode() {
		return createUserCode;
	}
	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Date getOperateTime() {
		return operateTime!=null?(Date)operateTime.clone():null;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime!=null?(Date)operateTime.clone():null;
	}
	public Integer getQueueStatus() {
		return queueStatus;
	}
	public void setQueueStatus(Integer queueStatus) {
		this.queueStatus = queueStatus;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getEnd() {
		return end;
	}
	public void setEnd(Integer end) {
		this.end = end;
	}
	public String getPrintStartTime() {
		return printStartTime;
	}
	public void setPrintStartTime(String printStartTime) {
		this.printStartTime = printStartTime;
	}
	public String getPrintEndTime() {
		return printEndTime;
	}
	public void setPrintEndTime(String printEndTime) {
		this.printEndTime = printEndTime;
	}
	public Integer getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

	public String getProvinceAgencyCode() {
		return provinceAgencyCode;
	}

	public void setProvinceAgencyCode(String provinceAgencyCode) {
		this.provinceAgencyCode = provinceAgencyCode;
	}

	public String getAreaHubCode() {
		return areaHubCode;
	}

	public void setAreaHubCode(String areaHubCode) {
		this.areaHubCode = areaHubCode;
	}
}
