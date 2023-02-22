package com.jd.bluedragon.external.crossbow.postal.enums;
/**
 * 1保价，2非保价
 * @author wuyoude
 *
 */
public enum InsuranceFlagEnum {
	Y("1"),
	N("2")
	;
	private InsuranceFlagEnum(String code) {
		this.code = code;
	}
	private String code;

	public String getCode() {
		return code;
	}
}
