package com.jd.bluedragon.common.dto.work;

/**
 * @ClassName WorkTaskStatusEnum
 * @Description 巡检状态-0：待分配 1：未完成 2：处理中 3：已完成  4:超时未完成
 * @Author wyd
 * @Date 2023/5/30 16:49
 **/
public enum WorkTaskQueryStatusEnum {

	TODO(1, "待处理"),
	COMPLETE(3, "已完成"),
	OVER_TIME(4, "已超时"),
    TRANSFERED(99, "已转派")
    ;

	private WorkTaskQueryStatusEnum(Integer code, String name) {
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
		WorkTaskQueryStatusEnum data = getEnum(code);
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
    public static WorkTaskQueryStatusEnum getEnum(Integer code) {
        for (WorkTaskQueryStatusEnum value : WorkTaskQueryStatusEnum.values()) {
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
