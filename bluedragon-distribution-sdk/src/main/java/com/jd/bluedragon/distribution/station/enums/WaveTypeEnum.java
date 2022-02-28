package com.jd.bluedragon.distribution.station.enums;

/**
 * @ClassName WaveTypeEnum
 * @Description 班次类型
 * @Author wyd
 * @Date 2022/1/4 16:49
 **/
public enum WaveTypeEnum {
	DAY(1,"白班"),
	MIDDLE(2,"中班"),
	NIGHT(3,"晚班")
    ;
	
	private WaveTypeEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	private Integer code;
	private String name;
	
	private static String ALL_NAMES = "";
	
	static{
        for (WaveTypeEnum value : WaveTypeEnum.values()) {
        	ALL_NAMES += value.name + " ";
        }
	}
	/**
	 * 获取所有名称描述
	 * @return
	 */
	public static String getAllNames() {
		return ALL_NAMES;
	}
	/**
	 * 根据code获取名称
	 * @param code
	 * @return
	 */
    public static String getNameByCode(Integer code) {
    	WaveTypeEnum data = getEnum(code);
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
    public static WaveTypeEnum getEnum(Integer code) {
        for (WaveTypeEnum value : WaveTypeEnum.values()) {
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
