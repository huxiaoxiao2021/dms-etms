package com.jd.bluedragon.distribution.sendprint.domain;

import java.io.Serializable;

public class PrintQueryCriteria implements Serializable{

	private static final long serialVersionUID = 4288745693505736942L;

	/** 分拣中心  */
	private Integer siteCode;
	
	/** 站点  */
	private Integer receiveSiteCode;
	
	/** 交接批次  */
	private String sendCode;
	
	/** 发货开始时间  */
	private String startTime;
	
	/** 发货结束时间  */
	private String endTime;
	
	/** 箱号  */
    private String boxCode;
    
    /** 发货人 */
    private Integer sendUserCode;
    
    /** 包裹号*/
    private String packageBarcode;
    
    /** 运单号*/
    private String waybillcode;
    
    /**库别 */
    private Integer fc;
    
    /** 211  */
    private boolean is211;

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 页码
     */
    private Integer pageNo;
	
    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
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

    public Integer getFc() {
        return fc;
    }

    public void setFc(Integer fc) {
        this.fc = fc;
    }

    public boolean isIs211() {
        return is211;
    }

    public void setIs211(boolean is211) {
        this.is211 = is211;
    }

    public Integer getSendUserCode() {
        return sendUserCode;
    }

    public void setSendUserCode(Integer sendUserCode) {
        this.sendUserCode = sendUserCode;
    }

	public String getWaybillcode() {
		return waybillcode;
	}

	public void setWaybillcode(String waybillcode) {
		this.waybillcode = waybillcode;
	}

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
