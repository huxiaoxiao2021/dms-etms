package com.jd.bluedragon.distribution.reverse.domain;

import java.util.Date;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-11-27 下午04:31:41
 *
 * 商品明细对象-备件库
 */
public class ReverseSpare {

	/**
	 * 自增主键
	 */
	private Long systemId;
	
	/**
	 * 备件编码 
	 */
	private String spareCode;
	
	/**
	 * 发货批次
	 */
	private String sendCode;
	
	/**
	 * 运单号
	 */
	private String waybillCode;
	
	/**
	 * 商品编号
	 */
	private String productId;
	
	/**
	 * 商品编码
	 */
	private String productCode;
	
	/**
	 * 商品名称
	 */
	private String productName;
	
	/**
	 * 商品价格
	 */
	private Double productPrice;
	
	/**
	 * 属性1编号
	 * 	商品外包装 
	 */
	private Integer arrtCode1;
	
	/**
	 * 属性1描述
	 * 	新；非新；无 
	 */
	private String arrtDesc1;
	
	/**
	 * 属性2编号
	 * 	主商品外观
	 */
	private Integer arrtCode2;
	
	/**
	 * 属性2描述
	 * 	新；轻微损；严重损
	 */
	private String arrtDesc2;
	
	/**
	 * 属性3编号
	 * 	主商品功能
	 */
	private Integer arrtCode3;
	
	/**
	 * 属性3描述
	 * 	坏；待检测 
	 */
	private String arrtDesc3;
	
	/**
	 * 属性4编号
	 * 	附件情况 
	 */
	private Integer arrtCode4;
	
	/**
	 * 属性4描述
	 * 	完整；不完整；
	 */
	private String arrtDesc4;
	
	private Date createTime;
	
	private Date updateTime;
	
	private Integer yn;
	
	 /**
     * 备件库入库单号
     */
    private String spareTranCode;

	/**
	 * 用于对接备件库的唯一标识
	 */
	private String waybillSendCode;

	public String getSpareTranCode() {
		return spareTranCode;
	}

	public void setSpareTranCode(String spareTranCode) {
		this.spareTranCode = spareTranCode;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public String getSpareCode() {
		return spareCode;
	}

	public void setSpareCode(String spareCode) {
		this.spareCode = spareCode;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Double productPrice) {
		this.productPrice = productPrice;
	}

	public Integer getArrtCode1() {
		return arrtCode1;
	}

	public void setArrtCode1(Integer arrtCode1) {
		this.arrtCode1 = arrtCode1;
	}

	public String getArrtDesc1() {
		return arrtDesc1;
	}

	public void setArrtDesc1(String arrtDesc1) {
		this.arrtDesc1 = arrtDesc1;
	}

	public Integer getArrtCode2() {
		return arrtCode2;
	}

	public void setArrtCode2(Integer arrtCode2) {
		this.arrtCode2 = arrtCode2;
	}

	public String getArrtDesc2() {
		return arrtDesc2;
	}

	public void setArrtDesc2(String arrtDesc2) {
		this.arrtDesc2 = arrtDesc2;
	}

	public Integer getArrtCode3() {
		return arrtCode3;
	}

	public void setArrtCode3(Integer arrtCode3) {
		this.arrtCode3 = arrtCode3;
	}

	public String getArrtDesc3() {
		return arrtDesc3;
	}

	public void setArrtDesc3(String arrtDesc3) {
		this.arrtDesc3 = arrtDesc3;
	}

	public Integer getArrtCode4() {
		return arrtCode4;
	}

	public void setArrtCode4(Integer arrtCode4) {
		this.arrtCode4 = arrtCode4;
	}

	public String getArrtDesc4() {
		return arrtDesc4;
	}

	public void setArrtDesc4(String arrtDesc4) {
		this.arrtDesc4 = arrtDesc4;
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

	public String getWaybillSendCode() {
		return waybillSendCode;
	}

	public void setWaybillSendCode(String waybillSendCode) {
		this.waybillSendCode = waybillSendCode;
	}
}
