package com.jd.bluedragon.external.crossbow.postal.enums;
/**
 * 0：国内 3：国际
 * @author wuyoude
 *
 */
public enum TraceTypeEnum {
	INTERNAL("0"),
	INTERNATIONAL("3")
	;
	private TraceTypeEnum(String code) {
		this.code = code;
	}
	private String code;

	public String getCode() {
		return code;
	}
}
