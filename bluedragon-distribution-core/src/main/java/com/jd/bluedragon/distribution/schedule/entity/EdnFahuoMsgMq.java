package com.jd.bluedragon.distribution.schedule.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: EdnFahuoMsgMq
 * @Description: 企配仓消息体
 * @author wuyoude
 * @date 2020年04月29日 17:59:55
 *
 */
public class EdnFahuoMsgMq implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 企配单号
	 */
	private String ednNum;

	/**
	 * 企配单批次号
	 */
	private String ednBatchNum;
	
	/**
	 * 父单号
	 */
	private String parentOrderId;
	
	/**
	 * 操作时间
	 */
	private Date operationTime;
	
	private List<Order> orderList;
	
	public static class Order implements Serializable {

		private static final long serialVersionUID = 1L;
		
		/**
		 * 子订单号
		 */
		private String orderId;
		
		/**
		 * 运单号
		 */
		private String waybillCode;
		
		/**
		 * b2b仓运单号
		 */
		private String eclpWaybillCode;

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getWaybillCode() {
			return waybillCode;
		}

		public void setWaybillCode(String waybillCode) {
			this.waybillCode = waybillCode;
		}

		public String getEclpWaybillCode() {
			return eclpWaybillCode;
		}

		public void setEclpWaybillCode(String eclpWaybillCode) {
			this.eclpWaybillCode = eclpWaybillCode;
		}
	}

	public String getEdnNum() {
		return ednNum;
	}

	public void setEdnNum(String ednNum) {
		this.ednNum = ednNum;
	}

	public String getEdnBatchNum() {
		return ednBatchNum;
	}

	public void setEdnBatchNum(String ednBatchNum) {
		this.ednBatchNum = ednBatchNum;
	}

	public String getParentOrderId() {
		return parentOrderId;
	}

	public void setParentOrderId(String parentOrderId) {
		this.parentOrderId = parentOrderId;
	}

	public Date getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(Date operationTime) {
		this.operationTime = operationTime;
	}

	public List<Order> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}
}
