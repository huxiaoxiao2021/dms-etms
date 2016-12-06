package com.jd.bluedragon.distribution.popPrint.dto;

public class PopSigninDto {
	public String queueNo;
	public String thirdWaybillCode;
	public Integer createUserCode;
	public String  createUser;
	public String signStartTime;
	public String signEndTime;
	public Integer createSiteCode;
	public String expressCode;
	public Integer start;
	public Integer end;
    private Integer pageSize;
	public String getQueueNo() {
		return queueNo;
	}
	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}
	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}
	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
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
	public String getSignStartTime() {
		return signStartTime;
	}
	public void setSignStartTime(String signStartTime) {
		this.signStartTime = signStartTime;
	}
	public String getSignEndTime() {
		return signEndTime;
	}
	public void setSignEndTime(String signEndTime) {
		this.signEndTime = signEndTime;
	}
	public Integer getCreateSiteCode() {
		return createSiteCode;
	}
	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}
	public String getExpressCode() {
		return expressCode;
	}
	public void setExpressCode(String expressCode) {
		this.expressCode = expressCode;
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

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
