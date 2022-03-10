package com.jd.bluedragon.common.dto.station;

import java.io.Serializable;

/**
 * 上岗码记录
 *
 * @author hujiping
 * @date 2022/2/25 5:25 PM
 */
public class ScanUserData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

	/**
	 * 员工ERP|拼音|身份证号
	 */
	private String userCode;

	/**
	 * 
	 * 工种:1-正式工 2-派遣工 3-外包工 4-临时工5-小时工
	 */
	private Integer jobCode;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public Integer getJobCode() {
		return jobCode;
	}

	public void setJobCode(Integer jobCode) {
		this.jobCode = jobCode;
	}
}
