package com.jd.bluedragon.distribution.api.response;

import java.util.Date;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-14 下午09:19:39
 *
 * POP打印信息Rest返回对象
 */
public class PopPrintResponse extends JdResponse {
	private static final long serialVersionUID = 1L;
	
	public static final Integer CODE_OK_NULL = 2200;
	
	public static final String MESSAGE_OK_NULL = "调用服务成功，数据为空";
	
	public PopPrintResponse() {
		super();
	}
	
	public PopPrintResponse(Integer code, String message) {
		super(code, message);
		
	}
	
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
	 * 打印包裹人编号
	 */
	private Integer printPackCode;
	
	/**
	 * 打印包裹时间
	 */
	private Date printPackTime;
	
	/**
	 * 打印发票人编号
	 */
	private Integer printInvoiceCode;
	
	/**
	 * 打印发票时间
	 */
	private Date printInvoiceTime;
	
	/**
     * 打印次数
     */
    private Integer printCount;
	
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

	public Integer getPrintCount() {
		return printCount;
	}

	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
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
