package com.jd.bluedragon.distribution.spare.domain;

import java.util.Date;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-11-27 下午04:31:41
 *
 * 配送损-备件库 销售金额明细
 */
public class SpareSale {

	/**
	 * 自增主键
	 */
	private Long id;
	
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
	private Date saleTime;
	
	private Date createTime;
	
	private Date updateTime;
	
	private Integer yn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Date getSaleTime() {
		return saleTime!=null?(Date)saleTime.clone():null;
	}

	public void setSaleTime(Date saleTime) {
		this.saleTime = saleTime!=null?(Date)saleTime.clone():null;
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
