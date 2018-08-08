package com.jd.bluedragon.distribution.popPrint.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-14 下午07:28:28
 *
 * POP打印结果类
 */
public class PopPrint implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final Integer POP_FLAF_1=1;
	
	/**
	 * 主键ID
	 */
	private Long popPrintId;
	
	/**
	 * 运单号
	 */
	private String waybillCode;
	
	/**
	 * 创建站点编号
	 */
	private Integer createSiteCode;
	
	/**
	 * 创建站点名称
	 */
	private String createSiteName;
	
	/**
	 * 打印包裹人编号
	 */
	private Integer printPackCode;
	
	/**
	 * 打印包裹人名称
	 */
	private String printPackUser;
	
	/**
	 * 打印包裹时间
	 */
	private Date printPackTime;
	
	/**
	 * 打印发票人编号
	 */
	private Integer printInvoiceCode;
	
	/**
	 * 打印发票人名称
	 */
	private String printInvoiceUser;
	
	/**
	 * 打印发票时间
	 */
	private Date printInvoiceTime;
	
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
	
	/**
     * POP商家ID
     */
    private Integer popSupId;
    
    /**
     * POP商家名称
     */
    private String popSupName;
    
    /**
     * 包裹数量
     */
    private Integer quantity;
    
    /**
     * 滑道号
     */
    private String crossCode;

    /** 运单类型(JYN) */
    private Integer waybillType;
    
    /**
     * POP收货类型：
     * 	商家直送：1
     * 	托寄送货：2
     */
    private Integer popReceiveType;
    
    /**
     * 打印次数
     */
    private Integer printCount;
    
    /**
	 * 三方运单号
	 */
	private String thirdWaybillCode;
	
	/**
     * 排队号
     */
    private String queueNo;
    
    private String packageBarcode;
    
    private Integer operateType;
    
    private Integer createUserCode;
    
    private String createUser;
    
    private String boxCode;
    
    private String driverCode;
    
    private String driverName;
    
    /**
	 * B商家ID
	 */
	private Integer busiId;
	
	/**
	 * B商家名称
	 */
	private String busiName;

	public Long getPopPrintId() {
		return popPrintId;
	}

	public void setPopPrintId(Long popPrintId) {
		this.popPrintId = popPrintId;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Integer getPrintPackCode() {
		return printPackCode;
	}

	public void setPrintPackCode(Integer printPackCode) {
		this.printPackCode = printPackCode;
	}

	public Date getPrintPackTime() {
		return printPackTime!=null?(Date)printPackTime.clone():null;
	}

	public void setPrintPackTime(Date printPackTime) {
		this.printPackTime = printPackTime!=null?(Date)printPackTime.clone():null;
	}

	public Integer getPrintInvoiceCode() {
		return printInvoiceCode;
	}

	public void setPrintInvoiceCode(Integer printInvoiceCode) {
		this.printInvoiceCode = printInvoiceCode;
	}

	public Date getPrintInvoiceTime() {
		return printInvoiceTime!=null?(Date)printInvoiceTime.clone():null;
	}

	public void setPrintInvoiceTime(Date printInvoiceTime) {
		this.printInvoiceTime = printInvoiceTime!=null?(Date)printInvoiceTime.clone():null;
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

	public String getCreateSiteName() {
		return createSiteName;
	}

	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	public String getPrintPackUser() {
		return printPackUser;
	}

	public void setPrintPackUser(String printPackUser) {
		this.printPackUser = printPackUser;
	}

	public String getPrintInvoiceUser() {
		return printInvoiceUser;
	}

	public void setPrintInvoiceUser(String printInvoiceUser) {
		this.printInvoiceUser = printInvoiceUser;
	}

	public Integer getPopSupId() {
		return popSupId;
	}

	public void setPopSupId(Integer popSupId) {
		this.popSupId = popSupId;
	}

	public String getPopSupName() {
		return popSupName;
	}

	public void setPopSupName(String popSupName) {
		this.popSupName = popSupName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getCrossCode() {
		return crossCode;
	}

	public void setCrossCode(String crossCode) {
		this.crossCode = crossCode;
	}

	public Integer getWaybillType() {
		return waybillType;
	}

	public void setWaybillType(Integer waybillType) {
		this.waybillType = waybillType;
	}

	public Integer getPopReceiveType() {
		return popReceiveType;
	}

	public void setPopReceiveType(Integer popReceiveType) {
		this.popReceiveType = popReceiveType;
	}

	public Integer getPrintCount() {
		return printCount;
	}

	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
	}

	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}

	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
	}

	public String getQueueNo() {
		return queueNo;
	}

	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Integer getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getDriverCode() {
		return driverCode;
	}

	public void setDriverCode(String driverCode) {
		this.driverCode = driverCode;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public Integer getBusiId() {
		return busiId;
	}

	public void setBusiId(Integer busiId) {
		this.busiId = busiId;
	}

	public String getBusiName() {
		return busiName;
	}

	public void setBusiName(String busiName) {
		this.busiName = busiName;
	}
}
