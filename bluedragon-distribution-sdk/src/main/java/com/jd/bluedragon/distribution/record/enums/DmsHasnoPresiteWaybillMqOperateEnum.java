package com.jd.bluedragon.distribution.record.enums;

/**
 * 动作枚举
 *
 */
public enum DmsHasnoPresiteWaybillMqOperateEnum {
    /**
     * 待换单
     */
	INIT(0, "初始化"),
    /**
     * 验货
     */
	CHECK(1, "验货"),

    /**
     * 外呼成功
     */
    CALL_SUC(2, "外呼成功"),
    /**
     * 外呼失败
     */
    CALL_FAIL(3, "外呼失败"),
    /**
     * 弃货
     */
    WASTE(4, "弃货"),

    /**
     * 换单
     */
    EXCHANGE(5, "换单"),
    /**
     * 补打
     */
    REPRINT(6, "补打")
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
		for(DmsHasnoPresiteWaybillMqOperateEnum enumData:DmsHasnoPresiteWaybillMqOperateEnum.values()) {
			if(enumData.getCode().equals(code)) {
				return enumData.getName();
			}
		}
        return "";
    }

    DmsHasnoPresiteWaybillMqOperateEnum(Integer code, String name) {
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
