package com.jd.bluedragon.distribution.exportlog.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.Date;

public class ExportLogCondition   extends BasePagerCondition {
	private static final long serialVersionUID = 1L;
	private Long id;
	/**
     * 导出编号
	 */
	private String exportCode;
	/**
     * erp
	 */
	private String createUser;
	/**
	 * 操作时间
	 * */
	private String startTime;

	/**
	 * 类型  1：银行卡校验导出
	 */
	private Integer type;

	/**
	 * 操作时间
	 * */
	private String endTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExportCode() {
		return exportCode;
	}

	public void setExportCode(String exportCode) {
		this.exportCode = exportCode;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
