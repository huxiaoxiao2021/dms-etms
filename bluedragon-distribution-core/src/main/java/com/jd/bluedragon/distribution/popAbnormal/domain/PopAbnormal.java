package com.jd.bluedragon.distribution.popAbnormal.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-21 下午01:49:16
 *
 * POP差异订单对象
 */
public class PopAbnormal implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 已发申请，商家未处理
	 */
	public static final Integer POP_UNPASS = 1;
	
	/**
	 * 商家审核完毕，已处理
	 */
	public static final Integer POP_PASSED = 2;
	
	/**
	 * 主键ID
	 */
	private Long id;
	
	/**
	 * 流水单号
	 */
	private String serialNumber;
	
	/**
	 * 类型
	 */
	private Integer abnormalType;
	
	/**
	 * 运单号
	 */
	private String waybillCode;
	
	/**
	 * 订单号
	 */
	private String orderCode;
	
	/**
	 * POP商家编号
	 */
	private String popSupNo;
	
	/**
	 * POP商家名称
	 */
	private String popSupName;
	
	/**
	 * 原始包裹数量
	 */
	private Integer currentNum;
	
	/**
	 * 实际包裹数量
	 */
	private Integer actualNum;
	
	/**
	 * 商家确认包裹数量
	 */
	private Integer confirmNum;
	
	/**
	 * 操作人Code
	 */
	private Integer operatorCode;
	
	/**
	 * 操作人名称
	 */
	private String operatorName;
	
	/**
	 * 操作站点编号
	 */
	private Integer createSiteCode;
	
	/**
	 * 操作站点名称
	 */
	private String createSiteName;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 商家审核通过时间
	 */
	private Date confirmTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * POP申请单状态
	 */
	private Integer abnormalState;
	
	/**
	 * 备注
	 */
	private String memo;
	
	/**
	 * 是否有效
	 */
	private Integer yn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Integer getAbnormalType() {
		return abnormalType;
	}

	public void setAbnormalType(Integer abnormalType) {
		this.abnormalType = abnormalType;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
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

	public String getPopSupNo() {
		return popSupNo;
	}

	public void setPopSupNo(String popSupNo) {
		this.popSupNo = popSupNo;
	}
	
	public String getPopSupName() {
		return popSupName;
	}

	public void setPopSupName(String popSupName) {
		this.popSupName = popSupName;
	}

	public Integer getCurrentNum() {
		return currentNum;
	}

	public void setCurrentNum(Integer currentNum) {
		this.currentNum = currentNum;
	}

	public Integer getActualNum() {
		return actualNum;
	}

	public void setActualNum(Integer actualNum) {
		this.actualNum = actualNum;
	}

	public Integer getConfirmNum() {
		return confirmNum;
	}

	public void setConfirmNum(Integer confirmNum) {
		this.confirmNum = confirmNum;
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

	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}

	public Date getConfirmTime() {
		return confirmTime!=null?(Date)confirmTime.clone():null;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime!=null?(Date)confirmTime.clone():null;
	}

	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}

	public Integer getAbnormalState() {
		return abnormalState;
	}

	public void setAbnormalState(Integer abnormalState) {
		this.abnormalState = abnormalState;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}
}
