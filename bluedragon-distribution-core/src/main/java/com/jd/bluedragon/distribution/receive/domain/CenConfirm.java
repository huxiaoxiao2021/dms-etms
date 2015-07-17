package com.jd.bluedragon.distribution.receive.domain;

import java.util.Date;

public class CenConfirm implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5366873771896641567L;

	private Long confirmId;

	private String sendCode;

	private Date createTime;

	private Integer createSiteCode;

	private String waybillCode;

	private String boxCode;

	private String packageBarcode;

	private Short type;

	private Date updateTime;

	private Integer yn;

	private Short status;

	private Integer topNumber;

	private String thirdWaybillCode;

	/* 验货人 */
	private String inspectionUser;

	/* 验货人code */
	private Integer inspectionUserCode;

	/* 验货时间 */
	private Date inspectionTime;

	/* 验货单位Code */
	private Integer inspectionSiteCode;

	/* 操作类型--用与报表统计 */
	private Integer operateType;

	/* 取件单号 */
	private String pickupCode;

	private Date operateTime;
	private Integer operateUserCode;

	private String operateUser;

	private String receiveUser;
	private Integer receiveUserCode;
	private Integer receiveSiteCode;
	private Date receiveTime;
	
	/*异常类型(退货验货)*/
    private String exceptionType;

	public CenConfirm() {
		super();
	}

	public String getReceiveUser() {
		return receiveUser;
	}

	public void setReceiveUser(String receiveUser) {
		this.receiveUser = receiveUser;
	}

	public Integer getOperateUserCode() {
		return operateUserCode;
	}

	public void setOperateUserCode(Integer operateUserCode) {
		this.operateUserCode = operateUserCode;
	}

	public String getOperateUser() {
		return operateUser;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public void setOperateUser(String operateUser) {
		this.operateUser = operateUser;
	}

	public Integer getReceiveUserCode() {
		return receiveUserCode;
	}

	public void setReceiveUserCode(Integer receiveUserCode) {
		this.receiveUserCode = receiveUserCode;
	}

	public Date getReceiveTime() {
		return receiveTime!=null?(Date)receiveTime.clone():null;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime!=null?(Date)receiveTime.clone():null;
	}

	public Date getOperateTime() {
		return operateTime!=null?(Date)operateTime.clone():null;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime!=null?(Date)operateTime.clone():null;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public String getPickupCode() {
		return pickupCode;
	}

	public void setPickupCode(String pickupCode) {
		this.pickupCode = pickupCode;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getInspectionUser() {
		return inspectionUser;
	}

	public void setInspectionUser(String inspectionUser) {
		this.inspectionUser = inspectionUser;
	}

	public Integer getInspectionUserCode() {
		return inspectionUserCode;
	}

	public void setInspectionUserCode(Integer inspectionUserCode) {
		this.inspectionUserCode = inspectionUserCode;
	}

	public Date getInspectionTime() {
		return inspectionTime!=null?(Date)inspectionTime.clone():null;
	}

	public void setInspectionTime(Date inspectionTime) {
		this.inspectionTime = inspectionTime!=null?(Date)inspectionTime.clone():null;
	}

	public Integer getInspectionSiteCode() {
		return inspectionSiteCode;
	}

	public void setInspectionSiteCode(Integer inspectionSiteCode) {
		this.inspectionSiteCode = inspectionSiteCode;
	}

	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}

	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
	}

	public Long getConfirmId() {
		return confirmId;
	}

	public void setConfirmId(Long confirmId) {
		this.confirmId = confirmId;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Integer getTopNumber() {
		return topNumber;
	}

	public void setTopNumber(Integer topNumber) {
		this.topNumber = topNumber;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}
	
	public static  class Builder{
		/*Required parameters*/
	    /*包裹号*/
	    private String packageBarcode;
	    
	    /*操作单位Code*/
	    private Integer createSiteCode;
	    
	    /*Optional parameters*/
	    /*收货单位Code*/
	    private Integer receiveSiteCode;
	    
	    /*箱号*/
	    private String boxCode;
	    
	    /*更新时间*/
	    private Date updateTime;
	    
	    private Short type;
	    
	    public Builder(String packageBarcode, Integer createSiteCode) {
			super();
			this.packageBarcode = packageBarcode;
			this.createSiteCode = createSiteCode;
		}
	    
	    public Builder receiveSiteCode(Integer val){
	    	receiveSiteCode = val;
	    	return this;
	    }
	    
	    public Builder boxCode(String val){
	    	boxCode = val;
	    	return this;
	    }
	    
	    public Builder updateTime( Date val ){
	    	updateTime = val!=null?(Date)val.clone():null;
	    	return this;
	    }
	    
	    public Builder type( Integer val ){
	    	type = val.shortValue();
	    	return this;
	    }
	    
	    public CenConfirm build(){
	    	return new CenConfirm(this);
	    }
	}
	
	public CenConfirm(Builder builder) {
		this.packageBarcode = builder.packageBarcode;
		this.createSiteCode = builder.createSiteCode;
		this.boxCode = builder.boxCode;
		this.receiveSiteCode = builder.receiveSiteCode;
		this.updateTime = builder.updateTime;
		this.type = builder.type;
	}
}