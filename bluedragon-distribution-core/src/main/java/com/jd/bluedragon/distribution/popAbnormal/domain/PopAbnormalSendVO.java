package com.jd.bluedragon.distribution.popAbnormal.domain;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2013-3-13 上午11:43:17
 *
 * 类说明
 */
public class PopAbnormalSendVO {
	
	public final static Integer FIRST_SEND = 1;
	public final static Integer SECOND_SEND = 2;
	

	/**
	 * 唯一标识字段：差异列表ID
	 */
	private Long serialNumber;
	
	private String waybillCode;
	
	/**
	 * 订单号
	 */
	private String orderCode;
	
	/**
	 * 一级类型
	 */
	private Integer mainType;
	
	/**
	 * 二级类型
	 */
	private Integer subType;
	
	/**
	 * 备注
	 */
	private String comment;
	
	/**
	 * 操作时间： yyyy-MM-dd hh:mm:ss
	 */
	private String operateTime;
	
	/**
	 * 字段1
	 */
	private String attr1;
	
	/**
	 * 字段2
	 */
	private String attr2;
	
	/**
	 * 传递次数(1或者2)
	 */
	private Integer retType;
	
	/**
	 * 商家ID
	 */
	private String popSupNo;
	
	/**
	 * 商家名称
	 */
	private String popSupName;

	public Long getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(Long serialNumber) {
		this.serialNumber = serialNumber;
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

	public Integer getMainType() {
		return mainType;
	}

	public void setMainType(Integer mainType) {
		this.mainType = mainType;
	}

	public Integer getSubType() {
		return subType;
	}

	public void setSubType(Integer subType) {
		this.subType = subType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
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

	public Integer getRetType() {
		return retType;
	}

	public void setRetType(Integer retType) {
		this.retType = retType;
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

}
