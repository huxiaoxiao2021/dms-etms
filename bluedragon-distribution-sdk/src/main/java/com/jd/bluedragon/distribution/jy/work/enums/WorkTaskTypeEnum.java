package com.jd.bluedragon.distribution.jy.work.enums;

/**
 * @ClassName WorkTaskTypeEnum
 * @Description 巡检任务类型
 * @Author wyd
 * @Date 2023/5/30 16:49
 **/
public enum WorkTaskTypeEnum {
	MEETING(1,"巡检例会"),
	MEETING_RECORD(2,"巡检例会记录"),
	WORKING (3,"巡检任务"),
    ;
	
	private WorkTaskTypeEnum(Integer code, String name) {
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
    	WorkTaskTypeEnum data = getEnum(code);
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
    public static WorkTaskTypeEnum getEnum(Integer code) {
        for (WorkTaskTypeEnum value : WorkTaskTypeEnum.values()) {
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
