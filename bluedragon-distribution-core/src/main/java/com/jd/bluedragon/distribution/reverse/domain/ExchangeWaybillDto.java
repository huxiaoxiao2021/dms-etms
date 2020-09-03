package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;

/**
 * 外单逆向换单
 *
 *  @param waybillCode  老运单号
 * @param operatorId  操作人ID
 * @param operatorName 操作人姓名
 * @param operateTime  操作时间
 * @param packageCount  包裹数（整单换单可不输入）
 * @param orgId   操作机构
 * @param createSiteCode 操作站
 * @param isTotal  是否为整单换单
 */
public class ExchangeWaybillDto implements Serializable{

    private static final long serialVersionUID = 1L;

    private String waybillCode ;

    public Integer operatorId ;

    private String operatorName ;

    private String operateTime ;

    private int packageCount ;

    private Integer orgId ;

    private Integer createSiteCode ;

    private boolean isTotalout ;

    private Integer returnType;

    private String address;

    private String contact; //联系人

    private String phone; //联系人电话
    /**
     * 二次换单标识
     */
    private Boolean twiceExchangeFlag = Boolean.FALSE;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public boolean getIsTotalout() {
        return isTotalout;
    }

    public void setisTotalout(boolean totalout) {
        isTotalout = totalout;
    }

    public Integer getReturnType() {
        return returnType;
    }

    public void setReturnType(Integer returnType) {
        this.returnType = returnType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

	public Boolean getTwiceExchangeFlag() {
		return twiceExchangeFlag;
	}

	public void setTwiceExchangeFlag(Boolean twiceExchangeFlag) {
		this.twiceExchangeFlag = twiceExchangeFlag;
	}
}
