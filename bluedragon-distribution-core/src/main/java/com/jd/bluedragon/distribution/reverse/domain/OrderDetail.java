package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;

/**
 * 对外（配送入）OrderDetail接口对象
 * @author qiYunFeng
 */
public class OrderDetail implements Serializable {

    private String wareId;

    private String wareName;

    private String partCode;

    private String serialNo;

    private String supplierId;

    private Boolean IsLuxury;

    private String mainWareFunctionType;

    private String mainWareAppearance;

    private String mainWarePackage;

    private String attachments;
	
	private Integer lossType;

    private static final long serialVersionUID = 1L;

	public void setWareId(String wareId) {
		this.wareId = wareId;
	}

	public String getWareId() {
		return wareId;
	}

	public void setWareName(String wareName) {
		this.wareName = wareName;
	}

	public String getWareName() {
		return wareName;
	}

	public void setMainWareFunctionType(String mainWareFunctionType) {
		this.mainWareFunctionType = mainWareFunctionType;
	}

	public String getMainWareFunctionType() {
		return mainWareFunctionType;
	}

	public void setMainWareAppearance(String mainWareAppearance) {
		this.mainWareAppearance = mainWareAppearance;
	}

	public String getMainWareAppearance() {
		return mainWareAppearance;
	}

	public void setPartCode(String partCode) {
		this.partCode = partCode;
	}

	public String getPartCode() {
		return partCode;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setIsLuxury(Boolean isLuxury) {
		IsLuxury = isLuxury;
	}

	public Boolean getIsLuxury() {
		return IsLuxury;
	}

	public void setMainWarePackage(String mainWarePackage) {
		this.mainWarePackage = mainWarePackage;
	}

	public String getMainWarePackage() {
		return mainWarePackage;
	}


	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public String getAttachments() {
		return attachments;
	}
	
	public Integer getLossType() {
		return lossType;
	}
	
	public void setLossType(Integer lossType) {
		this.lossType = lossType;
	}

}