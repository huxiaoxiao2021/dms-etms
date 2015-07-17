package com.jd.bluedragon.distribution.popAbnormal.domain;

import java.util.Date;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2013-2-27 下午02:13:38
 *
 * POP收货差异对象
 */
public class PopReceiveAbnormal {
	
	/**
	 * 待回复
	 */
	public final static Integer IS_SUBMIT = 1;
	/**
	 * 已回复
	 */
	public final static Integer IS_BACK = 2;
	/**
	 * 已完成
	 */
	public final static Integer IS_FINISH = 3;
	
	/**
	 *  自增主键 
	 */
	private Long abnormalId;
	
	/**
	 * 创建机构CODE
	 */
	private Integer orgCode;
	
	/**
	 * 创建机构名称
	 */
	private String orgName;
	
	/**
	 * 创建站点CODE
	 */
	private Integer createSiteCode;
	
	/**
	 * 创建站点名称 
	 */
	private String createSiteName;
	
	/**
	 * 一级差异类型ID
	 */
	private Integer mainType;
	
	/**
	 * 一级差异类型名称
	 */
	private String mainTypeName;
	
	/**
	 * 二级差异类型ID
	 */
	private Integer subType;
	
	/**
	 * 二级差异类型名称
	 */
	private String subTypeName;
	
	/**
	 * 发起人姓名
	 */
	private String operatorName;
	
	/**
	 * 运单号
	 */
	private String waybillCode;
	
	private Integer waybillType;
	
	/**
	 * 订单号
	 */
	private String orderCode;
	
	/**
	 * 商家ID
	 */
	private String popSupNo;
	
	/**
	 * 商家名称
	 */
	private String popSupName;
	
	/**
	 * 处理状态
	 */
	private Integer abnormalStatus;
	
	/**
	 * 字段1 
	 */
	private String attr1;
	
	/**
	 * 字段2 
	 */
	private String attr2;
	
	/**
	 * 字段3
	 */
	private String attr3;
	
	/**
	 * 字段4 
	 */
	private String attr4;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 是否有效
	 */
	private Integer yn;

	public Long getAbnormalId() {
		return abnormalId;
	}

	public void setAbnormalId(Long abnormalId) {
		this.abnormalId = abnormalId;
	}

	public Integer getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
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

	public Integer getMainType() {
		return mainType;
	}

	public void setMainType(Integer mainType) {
		this.mainType = mainType;
	}

	public String getMainTypeName() {
		return mainTypeName;
	}

	public void setMainTypeName(String mainTypeName) {
		this.mainTypeName = mainTypeName;
	}

	public Integer getSubType() {
		return subType;
	}

	public void setSubType(Integer subType) {
		this.subType = subType;
	}

	public String getSubTypeName() {
		return subTypeName;
	}

	public void setSubTypeName(String subTypeName) {
		this.subTypeName = subTypeName;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getWaybillType() {
		return waybillType;
	}

	public void setWaybillType(Integer waybillType) {
		this.waybillType = waybillType;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
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

	public Integer getAbnormalStatus() {
		return abnormalStatus;
	}

	public void setAbnormalStatus(Integer abnormalStatus) {
		this.abnormalStatus = abnormalStatus;
	}

	public String getAttr1() {
		return attr1;
	}

	public void setAttr1(String attr1) {
		this.attr1 = attr1;
	}

	public String getAttr2() {
		return attr2;
	}

	public void setAttr2(String attr2) {
		this.attr2 = attr2;
	}

	public String getAttr3() {
		return attr3;
	}

	public void setAttr3(String attr3) {
		this.attr3 = attr3;
	}

	public String getAttr4() {
		return attr4;
	}

	public void setAttr4(String attr4) {
		this.attr4 = attr4;
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

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}
}
