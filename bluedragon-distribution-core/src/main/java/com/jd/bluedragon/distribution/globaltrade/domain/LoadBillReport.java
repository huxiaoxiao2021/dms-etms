package com.jd.bluedragon.distribution.globaltrade.domain;

import java.io.Serializable;
import java.util.Date;

public class LoadBillReport implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 全局ID */
	private String id;

	/** 装载单申报ID */
	private String reportId;

	/** 装载单ID(随机号码),有多个,逗号隔开 */
	private String loadId;

	/** 仓库ID */
	private String warehouseId;

	/** 放行或作废时间(yyyy-mm-dd HH:mi:ss) */
	private Date processTime;

	/** 状态 1:成功，2:作废 */
	private Integer status;

	/** 备注 */
	private String notes;

	/** 成功的订单号,多个以逗号分割 */
	private String orderId;

    /**
     * 箱号
     */
    private String boxCode;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    /** 有效标识 */
	private Integer yn;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getLoadId() {
		return loadId;
	}

	public void setLoadId(String loadId) {
		this.loadId = loadId;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Date getProcessTime() {
		return processTime;
	}

	public void setProcessTime(Date processTime) {
		this.processTime = processTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

}
