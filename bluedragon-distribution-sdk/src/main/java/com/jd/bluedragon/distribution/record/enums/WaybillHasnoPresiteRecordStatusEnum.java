package com.jd.bluedragon.distribution.record.enums;

/**
 * 状态枚举
 *
 */
public enum WaybillHasnoPresiteRecordStatusEnum {
    /**
     * 待换单
     */
	INIT(0, "待外呼"),
    /**
     * 待换单
     */
	FOR_EXCHANGE(1, "待换单"),

    /**
     * 待逆向
     */
    FOR_REVERSE(2, "待逆向"),

    /**
     * 待弃货
     */
    FOR_WASTE(3, "待弃货"),

    /**
     * 已补打
     */
    REPRINT_FINISH(4, "已补打"),
    /**
     * 已换单
     */
    EXCHANGE_FINISH(5, "已换单"),
    /**
     * 已弃货
     */
    WASTE_FINISH(6, "已弃货"),
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
		for(WaybillHasnoPresiteRecordStatusEnum enumData:WaybillHasnoPresiteRecordStatusEnum.values()) {
			if(enumData.getCode().equals(code)) {
				return enumData.getName();
			}
		}
        return "";
    }

    WaybillHasnoPresiteRecordStatusEnum(Integer code, String name) {
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
