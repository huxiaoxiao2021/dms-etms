package com.jd.bluedragon.distribution.station.enums;

/**
 * 新班次类型
 */
public enum WaveTypeNewEnum {
	DAY1(1,"白班1"),
	DAY2(2,"白班2"),
	DAY3(3,"白班3"),
	MIDDLE1(4,"中班1"),
	MIDDLE2(5,"中班2"),
	MIDDLE3(6,"中班3"),
	NIGHT1(7,"晚班1"),
	NIGHT2(8,"晚班2"),
	NIGHT3(9,"晚班3"),
	DAY(10,"白班"),
	MIDDLE(11,"中班"),
	NIGHT(12,"晚班")
    ;

	private WaveTypeNewEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	private Integer code;
	private String name;
	
	private static String ALL_NAMES = "";
	
	static{
        for (WaveTypeNewEnum value : WaveTypeNewEnum.values()) {
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
    	WaveTypeNewEnum data = getEnum(code);
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
    public static WaveTypeNewEnum getEnum(Integer code) {
        for (WaveTypeNewEnum value : WaveTypeNewEnum.values()) {
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
