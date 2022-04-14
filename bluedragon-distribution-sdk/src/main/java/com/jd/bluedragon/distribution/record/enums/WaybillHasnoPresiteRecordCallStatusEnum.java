package com.jd.bluedragon.distribution.record.enums;

/**
 * 外呼状态枚举
 *
 */
public enum WaybillHasnoPresiteRecordCallStatusEnum {

    /**
     * 成功
     */
	SUC(1, "成功"),

    /**
     * 失败
     */
    FAIL(2, "失败"),
    ;

    private Integer code;

    private String name;

    /**
     * 通过code获取name
     *
     * @param code 编码
     * @return string
     */
    public static String getNameByCode(Integer code) {
		for(WaybillHasnoPresiteRecordCallStatusEnum enumData:WaybillHasnoPresiteRecordCallStatusEnum.values()) {
			if(enumData.getCode().equals(code)) {
				return enumData.getName();
			}
		}
        return "";
    }

    WaybillHasnoPresiteRecordCallStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
