package com.jd.bluedragon.common.domain;

public class DmsRouter implements java.io.Serializable {
	private static final long serialVersionUID = -3891293286657675794L;
	/** 类型 */
	private Integer type;

	/** 数据内容 */
	private String body;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
