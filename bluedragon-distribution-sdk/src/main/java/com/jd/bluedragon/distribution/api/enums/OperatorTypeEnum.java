package com.jd.bluedragon.distribution.api.enums;

/**
 * @ClassName 操作者类型
 * @Description 操作类型 0-未知类型 1-分拣客户端 2-自动化设备
 * @Author wyd
 * @Date 2022/11/15 16:49
 **/
public enum OperatorTypeEnum {
	UNKNOWN(0,"未知类型"),
	DMS_CLIENT(1,"分拣客户端"),
	AUTO_MACHINE(2,"自动化设备")
    ;
	
	private OperatorTypeEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	private Integer code;
	private String name;
	/**
	 * 根据code获取名称
	 * @param code
	 * @return
	 */
    public static String getNameByCode(Integer code) {
    	OperatorTypeEnum data = getEnum(code);
    	if(data != null) {
    		return data.getName();
    	}
        return null;
    }
	/**
	 * 根据code获取enum
	 * @param code
	 * @return
	 */
    public static OperatorTypeEnum getEnum(Integer code) {
        for (OperatorTypeEnum value : OperatorTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
    /**
     * 判断code是否存在
     * @param code
     * @return
     */
    public boolean exist(Integer code) {
        return null != getEnum(code);
    }

	public Integer getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
