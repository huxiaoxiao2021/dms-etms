package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class SpareRequest extends JdRequest {
    
    private static final long serialVersionUID = 8900218370299464985L;
    /**
     * 机构编号
     */
    private Integer orgId;
    /**
     * 库房站点编号
     */
    private Integer storeSiteId;
    /**
     * 库房号
     */
    private Integer storeId;
    
    /** 备件条码类型 */
    private String type;
    
    /** 备件条码编号 */
    private String spareCode;
    
    /** 数量 */
    private Integer quantity;
    
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getSpareCode() {
        return this.spareCode;
    }
    
    public void setSpareCode(String spareCode) {
        this.spareCode = spareCode;
    }
    
    public Integer getQuantity() {
        return this.quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "SpareRequest [type=" + this.type + ", spareCode=" + this.spareCode + ", quantity="
                + this.quantity + "]";
    }

	/**
	 * @return the orgId
	 */
	public Integer getOrgId() {
		return orgId;
	}

	/**
	 * @param orgId the orgId to set
	 */
	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	/**
	 * @return the storeId
	 */
	public Integer getStoreId() {
		return storeId;
	}

	/**
	 * @param storeId the storeId to set
	 */
	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	/**
	 * @return the storeSiteId
	 */
	public Integer getStoreSiteId() {
		return storeSiteId;
	}

	/**
	 * @param storeSiteId the storeSiteId to set
	 */
	public void setStoreSiteId(Integer storeSiteId) {
		this.storeSiteId = storeSiteId;
	}
    
}
