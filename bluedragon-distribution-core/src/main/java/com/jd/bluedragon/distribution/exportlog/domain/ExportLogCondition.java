package com.jd.bluedragon.distribution.exportlog.domain;

import java.io.Serializable;
import java.util.Date;

public class ExportLogCondition  implements Serializable {
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
}
