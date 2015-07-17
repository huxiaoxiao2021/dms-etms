package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-12-20 下午04:47:02
 *
 * 逆向接收配送损商品销售信息
 */
public class SpareSaleRequest extends JdRequest {

	private static final long serialVersionUID = 1L;

	/**
	 * 备件编码 
	 */
	private String spareCode;
	
	/**
	 * 商品编号
	 */
	private Integer productId;
	
	/**
	 * 商品名称
	 */
	private String productName;
	
	/**
	 * 销售金额
	 */
	private Double saleAmount;
	
	/**
	 * 销售时间
	 */
	private String saleTime;

	public String getSpareCode() {
		return spareCode;
	}

	public void setSpareCode(String spareCode) {
		this.spareCode = spareCode;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Double getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(Double saleAmount) {
		this.saleAmount = saleAmount;
	}

	public String getSaleTime() {
		return saleTime;
	}

	public void setSaleTime(String saleTime) {
		this.saleTime = saleTime;
	}
}
