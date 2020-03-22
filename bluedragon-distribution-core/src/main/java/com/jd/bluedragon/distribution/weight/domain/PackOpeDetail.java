package com.jd.bluedragon.distribution.weight.domain;

import java.io.Serializable;

/**
 * 包裹操作数据明细 （称重、测量体积业务需要）
 * @author ligang
 *
 */
public class PackOpeDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 包裹号
	 */
	private String packageCode;
	/**
	 * 包裹重量(KG)
	 */
	private Double pWeight;
	/**
	 * 包裹长(KG)
	 */
	private Double pLength;
	/**
	 * 包裹宽(KG)
	 */
	private Double pWidth;
	/**
	 * 包裹高(KG)
	 */
	private Double pHigh;
	/**
	 * 操作人ID
	 */
	private Integer opeUserId;
	/**
	 * 操作人姓名
	 */
	private String opeUserName;
	/**
	 * 操作单位ID
	 */
	private Integer opeSiteId;
	/**
	 * 操作单位名称
	 */
	private String opeSiteName;
	/**
	 * 操作时间(格式 yyyy-MM-dd HH:mm:ss )
	 */
	private String opeTime;
	/**
	 * 长包裹 0:普通包裹 1:长包裹
	 * */
	private Integer longPackage;

	public Integer getLongPackage() {
		return longPackage;
	}

	public void setLongPackage(Integer longPackage) {
		this.longPackage = longPackage;
	}

	public String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	public Double getpWeight() {
		return pWeight;
	}
	public void setpWeight(Double pWeight) {
		this.pWeight = pWeight;
	}
	public Double getpLength() {
		return pLength;
	}
	public void setpLength(Double pLength) {
		this.pLength = pLength;
	}
	public Double getpWidth() {
		return pWidth;
	}
	public void setpWidth(Double pWidth) {
		this.pWidth = pWidth;
	}
	public Double getpHigh() {
		return pHigh;
	}
	public void setpHigh(Double pHigh) {
		this.pHigh = pHigh;
	}
	public Integer getOpeUserId() {
		return opeUserId;
	}
	public void setOpeUserId(Integer opeUserId) {
		this.opeUserId = opeUserId;
	}
	public String getOpeUserName() {
		return opeUserName;
	}
	public void setOpeUserName(String opeUserName) {
		this.opeUserName = opeUserName;
	}
	public Integer getOpeSiteId() {
		return opeSiteId;
	}
	public void setOpeSiteId(Integer opeSiteId) {
		this.opeSiteId = opeSiteId;
	}
	public String getOpeSiteName() {
		return opeSiteName;
	}
	public void setOpeSiteName(String opeSiteName) {
		this.opeSiteName = opeSiteName;
	}
	public String getOpeTime() {
		return opeTime;
	}
	public void setOpeTime(String opeTime) {
		this.opeTime = opeTime;
	}
	
	
}
