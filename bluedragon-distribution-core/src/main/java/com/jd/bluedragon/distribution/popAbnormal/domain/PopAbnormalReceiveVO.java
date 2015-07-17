package com.jd.bluedragon.distribution.popAbnormal.domain;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2013-3-13 上午11:43:17
 *
 * 类说明
 */
public class PopAbnormalReceiveVO {
	
	public final static Integer NOT_END = 0;
	public final static Integer IS_END = 1;
	

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
	 * 是否结束
	 * 	0:未结束
	 * 	1:结束
	 */
	private Integer isEnd;
	
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

	public Integer getIsEnd() {
		return isEnd;
	}

	public void setIsEnd(Integer isEnd) {
		this.isEnd = isEnd;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
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
