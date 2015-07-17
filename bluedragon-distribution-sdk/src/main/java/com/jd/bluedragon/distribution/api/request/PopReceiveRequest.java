package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdObject;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-15 下午08:26:17
 *
 * POP托寄收货Request对象
 */
public class PopReceiveRequest extends JdObject {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 订单号
	 */
	private String waybillCode;
	
	/**
	 * 三方运单号
	 */
	private String thirdWaybillCode;
	
	/**
	 * 应收包裹数
	 */
	private Integer originalNum;
	
	/**
	 * 实收包裹数
	 */
	private Integer actualNum;
	
	/**
	 * 操作站点ID
	 */
	private Integer createSiteCode;
	
	/**
	 * 操作站点名称
	 */
	private String createSiteName;
	
	/**
	 * 操作人CODE
	 */
	private Integer operatorCode;
	
	/**
	 * 操作人名称
	 */
	private String operatorName;
	
	/**
	 * 操作时间
	 */
	private String operateTime;
	
	/**
	 * 是否跳过验证
	 */
	private Integer isPassCheck;

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}

	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
	}

	public Integer getOriginalNum() {
		return originalNum;
	}

	public void setOriginalNum(Integer originalNum) {
		this.originalNum = originalNum;
	}

	public Integer getActualNum() {
		return actualNum;
	}

	public void setActualNum(Integer actualNum) {
		this.actualNum = actualNum;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public String getCreateSiteName() {
		return createSiteName;
	}

	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	public Integer getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(Integer operatorCode) {
		this.operatorCode = operatorCode;
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

	public Integer getIsPassCheck() {
		return isPassCheck;
	}

	public void setIsPassCheck(Integer isPassCheck) {
		this.isPassCheck = isPassCheck;
	}
}
