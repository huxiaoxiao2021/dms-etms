package com.jd.bluedragon.distribution.reverse.domain;

public class ReverseReceiveLoss {
    
	/**
	 * 表示解锁，不锁定，可以对此单提报报损
	 */
	public final static int UNLOCK=1;
	
	/**
	 * 表示锁定，此单不可以再提报损
	 */
	public final static int LOCK=0;
	
	private String orderId;
	
	private Integer receiveType;
	
	private String updateDate;
	
	private String dmsId;

	private String dmsName;
	
	private String storeId;
	
	private String storeName;
	
	/**
	 * 表示单子是否要锁定，锁定的意思是此单不能再在报损系统提报
	 * 0 锁定
	 * 1 解锁
	 */
	private Integer isLock;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getReceiveType() {
		return receiveType;
	}

	public void setReceiveType(Integer receiveType) {
		this.receiveType = receiveType;
	}



	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getDmsId() {
		return dmsId;
	}

	public void setDmsId(String dmsId) {
		this.dmsId = dmsId;
	}

	public String getDmsName() {
		return dmsName;
	}

	public void setDmsName(String dmsName) {
		this.dmsName = dmsName;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public Integer getIsLock() {
		return isLock;
	}

	public void setIsLock(Integer isLock) {
		this.isLock = isLock;
	}
}
