package com.jd.bluedragon.distribution.schedule.vo;

import java.io.Serializable;
/**
 * 企配仓批次页面vo
 * @author wuyoude
 *
 */
public class DmsEdnBatchVo implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 企配仓批次号
	 */
	private String ednBatchNum;
	
	/**
	 * 交接单url
	 */
	private String deliveryReceiptUrl;

	public String getEdnBatchNum() {
		return ednBatchNum;
	}

	public void setEdnBatchNum(String ednBatchNum) {
		this.ednBatchNum = ednBatchNum;
	}

	public String getDeliveryReceiptUrl() {
		return deliveryReceiptUrl;
	}

	public void setDeliveryReceiptUrl(String deliveryReceiptUrl) {
		this.deliveryReceiptUrl = deliveryReceiptUrl;
	}
}
