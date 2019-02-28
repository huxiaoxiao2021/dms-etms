package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class SysConfigResponse extends JdResponse {

	private static final long serialVersionUID = 1422606529312362690L;

	public static final Integer CODE_OLD_PASSWORD_ERROR = 10001;
	public static final String MESSAGE_OLD_PASS_WORD_ERROR = "原密码错误.";

	public static final Integer CODE_NO_DATA = 10002;
	public static final String MESSAGE_NO_DATA = "无数据.";

	private Integer configType;
	private String configName;
	private String configContent;
	private Integer configOrder;

	public Integer getConfigType() {
		return configType;
	}

	public void setConfigType(Integer configType) {
		this.configType = configType;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getConfigContent() {
		return configContent;
	}

	public void setConfigContent(String configContent) {
		this.configContent = configContent;
	}

	public Integer getConfigOrder() {
		return configOrder;
	}

	public void setConfigOrder(Integer configOrder) {
		this.configOrder = configOrder;
	}
}
