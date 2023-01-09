package com.jd.bluedragon.external.crossbow.postal.enums;
/**
 * 4国内，5国际
 * @author wuyoude
 *
 */
public enum InternationalTypeEnum {
	INTERNAL("4"),
	INTERNATIONAL("5")
	;
	private InternationalTypeEnum(String code) {
		this.code = code;
	}
	private String code;

	public String getCode() {
		return code;
	}
}
