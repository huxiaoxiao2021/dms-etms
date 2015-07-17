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
public class PopAbnormalDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * POP商家编号
	 */
	private String popSupNo;

	/**
	 * 运单号
	 */
	private String waybillCode;
	
	/**
	 * 开始时间
	 */
	private Date startCreateTime;
	
	/**
	 * 结束时间
	 */
	private Date endCreateTime;

	/**
	 * POP申请单状态
	 */
	private Integer abnormalState;

	/**
	 * 操作人Code
	 */
	private Integer operatorCode;
	
	/**
	 * 订单号
	 */
	private String orderCode;
	
	/**
	 * 排序规则
	 */
	private String orderByString;

	public String getPopSupNo() {
		return popSupNo;
	}

	public void setPopSupNo(String popSupNo) {
		this.popSupNo = popSupNo;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Date getStartCreateTime() {
		return startCreateTime!=null?(Date)startCreateTime.clone():null;
	}

	public void setStartCreateTime(Date startCreateTime) {
		this.startCreateTime = startCreateTime!=null?(Date)startCreateTime.clone():null;
	}

	public Date getEndCreateTime() {
		return endCreateTime!=null?(Date)endCreateTime.clone():null;
	}

	public void setEndCreateTime(Date endCreateTime) {
		this.endCreateTime = endCreateTime!=null?(Date)endCreateTime.clone():null;
	}

	public Integer getAbnormalState() {
		return abnormalState;
	}

	public void setAbnormalState(Integer abnormalState) {
		this.abnormalState = abnormalState;
	}

	public Integer getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(Integer operatorCode) {
		this.operatorCode = operatorCode;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOrderByString() {
		return orderByString;
	}

	public void setOrderByString(String orderByString) {
		this.orderByString = orderByString;
	}
}
