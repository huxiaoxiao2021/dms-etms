package com.jd.bluedragon.distribution.popPickup.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class PopPickup implements Cloneable,java.io.Serializable,Comparable<PopPickup>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3339615438217878429L;

	private Long pickupId;
	
	/*运单号*/
    private String waybillCode;

    /*箱号*/
    private String boxCode;

    /*包裹号*/
    private String packageBarcode;

    /*状态*/
    private Integer pickupStatus;
    
    /*类型*/
    private Integer pickupType;
    
    /*运单下的包裹数量*/
    private Integer packageNumber;
    
    /*pop商家code*/
	private String popBusinessCode;
	
	/*pop商家名称*/
	private String popBusinessName;
	
	/*车牌号*/
	private String carCode;
	
	/*创建Code*/
    private Integer createSiteCode;
	
	/*接收站点Code*/
    private Integer receiveSiteCode;
	
	/*操作人*/
    private String createUser;

    /*操作人code*/
    private Integer createUserCode;
    
    /*更新人name*/
    private String updateUser;
    
    /*更新人code*/
    private Integer updateUserCode;

    /*创建时间*/
    private Date createTime;

    /*更新时间*/
    private Date updateTime;
    
    /*操作时间*/
    private Date operateTime;

    private Integer yn;
    
    /**
     * 运单类型
     */
    private Integer waybillType;

	public PopPickup() {
		super();
	}


	public Long getPickupId() {
		return pickupId;
	}

	public void setPickupId(Long pickupId) {
		this.pickupId = pickupId;
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


	public Integer getPickupStatus() {
		return pickupStatus;
	}


	public void setPickupStatus(Integer pickupStatus) {
		this.pickupStatus = pickupStatus;
	}


	public Integer getPickupType() {
		return pickupType;
	}


	public void setPickupType(Integer pickupType) {
		this.pickupType = pickupType;
	}


	public Integer getPackageNumber() {
		return packageNumber;
	}


	public void setPackageNumber(Integer packageNumber) {
		this.packageNumber = packageNumber;
	}


	public String getPopBusinessCode() {
		return popBusinessCode;
	}


	public void setPopBusinessCode(String popBusinessCode) {
		this.popBusinessCode = popBusinessCode;
	}


	public String getPopBusinessName() {
		return popBusinessName;
	}


	public void setPopBusinessName(String popBusinessName) {
		this.popBusinessName = popBusinessName;
	}


	public String getCarCode() {
		return carCode;
	}


	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}


	public Integer getCreateSiteCode() {
		return createSiteCode;
	}


	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}


	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}


	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}


	public String getCreateUser() {
		return createUser;
	}


	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}


	public Integer getCreateUserCode() {
		return createUserCode;
	}


	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}


	public String getUpdateUser() {
		return updateUser;
	}


	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}


	public Integer getUpdateUserCode() {
		return updateUserCode;
	}


	public void setUpdateUserCode(Integer updateUserCode) {
		this.updateUserCode = updateUserCode;
	}


	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = null!=createTime?(Date)createTime.clone():null;
	}


	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}


	public void setUpdateTime(Date updateTime) {
		this.updateTime = null!=updateTime?(Date)updateTime.clone():null;
	}


	public Date getOperateTime() {
		return operateTime!=null?(Date)operateTime.clone():null;
	}


	public void setOperateTime(Date operateTime) {
		this.operateTime = null!=operateTime?(Date)operateTime.clone():null;
	}


	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public Integer getWaybillType() {
		return waybillType;
	}

	public void setWaybillType(Integer waybillType) {
		this.waybillType = waybillType;
	}


	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


	@Override
	public int compareTo(PopPickup o) {
		if(null==this || null==o || StringUtils.isBlank(o.waybillCode))	{
			return 0;
		} else {
			return this.waybillCode.compareTo(o.waybillCode);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((boxCode == null) ? 0 : boxCode.hashCode());
		result = prime * result
				+ ((createSiteCode == null) ? 0 : createSiteCode.hashCode());
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result
				+ ((createUser == null) ? 0 : createUser.hashCode());
		result = prime * result
				+ ((createUserCode == null) ? 0 : createUserCode.hashCode());
		result = prime * result
				+ ((operateTime == null) ? 0 : operateTime.hashCode());
		result = prime * result
				+ ((packageBarcode == null) ? 0 : packageBarcode.hashCode());
		result = prime * result
				+ ((packageNumber == null) ? 0 : packageNumber.hashCode());
		result = prime * result
				+ ((pickupId == null) ? 0 : pickupId.hashCode());
		result = prime * result
				+ ((pickupStatus == null) ? 0 : pickupStatus.hashCode());
		result = prime * result
				+ ((pickupType == null) ? 0 : pickupType.hashCode());
		result = prime * result
				+ ((popBusinessCode == null) ? 0 : popBusinessCode.hashCode());
		result = prime * result
				+ ((popBusinessName == null) ? 0 : popBusinessName.hashCode());
		result = prime * result
				+ ((receiveSiteCode == null) ? 0 : receiveSiteCode.hashCode());
		result = prime * result
				+ ((updateTime == null) ? 0 : updateTime.hashCode());
		result = prime * result
				+ ((updateUser == null) ? 0 : updateUser.hashCode());
		result = prime * result
				+ ((updateUserCode == null) ? 0 : updateUserCode.hashCode());
		result = prime * result
				+ ((waybillCode == null) ? 0 : waybillCode.hashCode());
		result = prime * result + ((yn == null) ? 0 : yn.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PopPickup other = (PopPickup) obj;
		
		if (popBusinessCode == null) {
			if (other.popBusinessCode != null)
				return false;
		} else if (!popBusinessCode.equals(other.popBusinessCode))
			return false;
		if (waybillCode == null) {
			if (other.waybillCode != null)
				return false;
		} else if (!waybillCode.equals(other.waybillCode))
			return false;
		return true;
	}

	public static class Builder{
		/*运单号*/
	    private String waybillCode;

	    /*箱号*/
	    private String boxCode;

	    /*包裹号*/
	    private String packageBarcode;

	    /*状态*/
	    private Integer pickupStatus;
	    
	    /*类型*/
	    private Integer pickupType;
	    
	    /*运单下的包裹数量*/
	    private Integer packageNumber;
	    
	    /*pop商家code*/
		private String popBusinessCode;
		
		/*pop商家名称*/
		private String popBusinessName;
		
		/*车牌号*/
		private String carCode;
		
		/*创建站点Code*/
	    private Integer createSiteCode;
		
		/*接收站点Code*/
	    private Integer receiveSiteCode;
		
		/*操作人*/
	    private String createUser;

	    /*操作人code*/
	    private Integer createUserCode;
	    
	    /*更新人name*/
	    private String updateUser;
	    
	    /*更新人code*/
	    private Integer updateUserCode;

	    /*创建时间*/
	    private Date createTime;

	    /*更新时间*/
	    private Date updateTime;
	    
	    /*操作时间*/
	    private Date operateTime;

	    private Integer yn;
	    
	    private Integer waybillType;

		public Builder(String waybillCode, String packageBarcode,
				String popBusinessCode, String popBusinessName, Integer yn) {
			super();
			this.waybillCode = waybillCode;
			this.packageBarcode = packageBarcode;
			this.popBusinessCode = popBusinessCode;
			this.popBusinessName = popBusinessName;
			this.yn = yn;
		}
	    
	    public Builder waybillCode(String val){
	    	this.waybillCode = val;
	    	return this;
	    }
	    
	    public Builder boxCode(String val){
	    	this.boxCode = val;
	    	return this;
	    }
	    
	    public Builder packageBarcode(String val){
	    	this.packageBarcode =val;
	    	return this;
	    }
	    
	    public Builder pickupStatus(Integer val){
	    	this.pickupStatus = val;
	    	return this;
	    }
	    
	    public Builder pickupType(Integer val){
	    	this.pickupType = val;
	    	return this;
	    }
	    
	    public Builder packageNumber(Integer val){
	    	this.packageNumber = val;
	    	return this;
	    }
	    
	    public Builder popBusinessCode(String val){
	    	this.popBusinessCode = val;
	    	return this;
	    }
	    
	    public Builder popBusinessName(String val){
	    	this.popBusinessName = val;
	    	return this;
	    }
	    
	    public Builder carCode(String val){
	    	this.carCode = val;
	    	return this;
	    }
	    
	    public Builder createSiteCode(Integer val){
	    	this.createSiteCode = val;
	    	return this;
	    }
	    
	    public Builder receiveSiteCode(Integer val){
	    	this.receiveSiteCode = val;
	    	return this;
	    }
	    
	    public Builder createUser(String val){
	    	this.createUser = val;
	    	return this;
	    }
	    
	    public Builder createUserCode(Integer val){
	    	this.createUserCode = val;
	    	return this;
	    }
	    
	    public Builder updateUser(String val){
	    	this.updateUser = val;
	    	return this;
	    }
	    
	    public Builder updateUserCode(Integer val){
	    	this.updateUserCode = val;
	    	return this;
	    }
	    
	    public Builder createTime(Date val){
	    	this.createTime = val!=null?(Date)val.clone():null;
	    	return this;
	    }
	    
	    public Builder updateTime(Date val){
	    	this.updateTime = val!=null?(Date)val.clone():null;
	    	return this;
	    }
	    
	    public Builder operateTime(Date val){
	    	this.operateTime = val!=null?(Date)val.clone():null;
	    	return this;
	    }
	    
	    public Builder yn(Integer val){
	    	this.yn = val;
	    	return this;
	    }
	    
	    public Builder waybillType(Integer waybillType){
	    	this.waybillType = waybillType;
	    	return this;
	    }
	    
	    public PopPickup build(){
	    	return new PopPickup(this);
	    }
	}
	
	public PopPickup(Builder builder) {
		this.waybillCode = builder.waybillCode;
		this.boxCode = builder.boxCode;
		this.packageBarcode = builder.packageBarcode;
		this.pickupStatus = builder.pickupStatus;
		this.pickupType = builder.pickupType;
		this.packageNumber = builder.packageNumber;
		this.popBusinessCode = builder.popBusinessCode;
		this.popBusinessName = builder.popBusinessName;
		this.carCode = builder.carCode;
		this.createSiteCode = builder.createSiteCode;
		this.receiveSiteCode = builder.receiveSiteCode;
		this.createUser = builder.createUser;
		this.createUserCode = builder.createUserCode;
		this.updateUser = builder.updateUser;
		this.updateUserCode = builder.updateUserCode;
		this.createTime = builder.createTime;
		this.updateTime = builder.updateTime;
		this.operateTime = builder.operateTime;
		this.yn = builder.yn;
		this.waybillType = builder.waybillType;
	}
    
}
