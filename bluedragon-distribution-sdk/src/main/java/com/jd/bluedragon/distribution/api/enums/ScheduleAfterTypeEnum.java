package com.jd.bluedragon.distribution.api.enums;

/**
 * @ClassName ScheduleAfterTypeEnum
 * @Description 返调度回调类型
 * @Author wyd
 * @Date 2022/1/4 16:49
 **/
public enum ScheduleAfterTypeEnum {
	PACKAGE_CODE(1,"按包裹"),
	WAYBILL_CODE(2,"按运单"),
	PACKAGE_CODE_LIST(3,"按包裹列表"),
    ;
	
	private ScheduleAfterTypeEnum(Integer code, String name) {
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
    	ScheduleAfterTypeEnum data = getEnum(code);
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
    public static ScheduleAfterTypeEnum getEnum(Integer code) {
        for (ScheduleAfterTypeEnum value : ScheduleAfterTypeEnum.values()) {
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
