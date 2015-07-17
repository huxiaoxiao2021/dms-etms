package com.jd.bluedragon.distribution.send.domain.reverse;

import java.util.Date;

/**
 * 订单包裹验货明细(key:Id) className RevokeCheckOrderPackDetail
 * 
 * @author houxiaofang
 * @create 2011-8-6
 */
public class RevokeCheckOrderPackDetail {
	
	// id
	private int id;
	// 箱号
	private String boxNo;
	// 订单号
	private String orderNo;
	// 验货人
	private String operater;
	// 验货时间
	private Date createDate;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the boxNo
	 */
	public String getBoxNo() {
		return this.boxNo;
	}
	
	/**
	 * @param boxNo
	 *            the boxNo to set
	 */
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	
	/**
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return this.orderNo;
	}
	
	/**
	 * @param orderNo
	 *            the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	/**
	 * @return the operater
	 */
	public String getOperater() {
		return this.operater;
	}
	
	/**
	 * @param operater
	 *            the operater to set
	 */
	public void setOperater(String operater) {
		this.operater = operater;
	}
	
	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return this.createDate == null ? null : (Date) this.createDate.clone();
	}
	
	/**
	 * @param createDate
	 *            the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate == null ? null : (Date) createDate.clone();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof RevokeCheckOrderPackDetail)) {
			return false;
		}
		RevokeCheckOrderPackDetail orderPackDetail = (RevokeCheckOrderPackDetail) obj;
		if (this.orderNo == null) {
			return this.orderNo == orderPackDetail.orderNo;
		} else {
			return this.orderNo.equals(orderPackDetail.orderNo);
		}
	}
	
	@Override
	public int hashCode() {
		if (this.orderNo == null) {
			return super.hashCode();
		} else {
			return this.orderNo.hashCode();
		}
	}
	
}
