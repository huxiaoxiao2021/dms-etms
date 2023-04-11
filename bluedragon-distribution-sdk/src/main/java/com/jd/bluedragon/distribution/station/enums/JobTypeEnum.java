package com.jd.bluedragon.distribution.station.enums;

/**
 * @ClassName JobTypeEnum
 * @Description 工种类型
 * @Author wyd
 * @Date 2022/1/4 16:49
 **/
public enum JobTypeEnum {
	JOBTYPE1(1,"正式工"),
	JOBTYPE2(2,"派遣工"),
	JOBTYPE3(3,"外包工"),
	JOBTYPE4(4,"临时工"),
	JOBTYPE5(5,"小时工"),
    JOBTYPE6(6,"支援"),
	JOBTYPE7(7, "联盟工"),
    ;
	
	private JobTypeEnum(Integer code, String name) {
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
    	JobTypeEnum data = getEnum(code);
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
    public static JobTypeEnum getEnum(Integer code) {
        for (JobTypeEnum value : JobTypeEnum.values()) {
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
