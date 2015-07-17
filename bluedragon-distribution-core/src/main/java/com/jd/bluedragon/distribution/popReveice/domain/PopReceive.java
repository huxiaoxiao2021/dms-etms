package com.jd.bluedragon.distribution.popReveice.domain;

import java.util.Date;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2013-2-17 下午05:54:02
 *
 * Pop 托寄收货对象
 */
public class PopReceive {
	
	/**
	 * POP已正常收货
	 */
	public final static Integer POP_IS_REVERICE = 1;

	/**
	 * POP未正常收货
	 */
	public final static Integer POP_NOT_REVERICE = 0;
	
	/**
	 * 托寄跳过收货验证
	 */
	public final static Integer EXPRESS_PASS_CHECK = 1;
	
	/**
	 * 托寄收货跳过回传标识
	 */
	public final static String EXPRESS_THIRD_WAYBILLCODE = "000000";

	/**
	 * 主键ID
	 */
	private Long systemId;
	
	/**
	 * 收货类型
	 */
	private Integer receiveType;
	
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
	private Date operateTime;
	
	/**
	 * 系统创建时间
	 */
	private Date createTime;
	
	/**
	 * 系统更新时间
	 */
	private Date updateTime;
	
	/**
	 * 是否收全
	 */
	private Integer isReverse;
	
	/**
	 * 防重码
	 */
	private String fingerPrint;
	
	/**
	 * 是否有效
	 */
	private Integer yn;
	
	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public Integer getReceiveType() {
		return receiveType;
	}

	public void setReceiveType(Integer receiveType) {
		this.receiveType = receiveType;
	}

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

	public Date getOperateTime() {
		return operateTime!=null?(Date)operateTime.clone():null;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime!=null?(Date)operateTime.clone():null;
	}

	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}

	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}

	public Integer getIsReverse() {
		return isReverse;
	}

	public void setIsReverse(Integer isReverse) {
		this.isReverse = isReverse;
	}

	public String getFingerPrint() {
		return fingerPrint;
	}

	public void setFingerPrint(String fingerPrint) {
		this.fingerPrint = fingerPrint;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}
}
