package com.jd.bluedragon.external.crossbow.postal.enums;
/**
 * 1标准类产品，2经济类产品
 * @author wuyoude
 *
 */
public enum ProductTypeEnum {
	STANDARD("1"),
	ECONOMIC("2")
	;
	private ProductTypeEnum(String code) {
		this.code = code;
	}
	private String code;

	public String getCode() {
		return code;
	}
}
