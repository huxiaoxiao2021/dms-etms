package com.jd.bluedragon.distribution.jy.work.enums;

/**
 * @ClassName WorkTaskStatusEnum
 * @Description 巡检状态-0：待分配 1：未完成 2：处理中 3：已完成  4:超时未完成
 * @Description  注意：99-已转派 仅用于作业App查询用，无此状态
 * @Author wyd
 * @Date 2023/5/30 16:49
 **/
public enum WorkTaskStatusEnum {
	TO_DISTRIBUTION(0, "待分配"),
    TODO(1, "待处理"),
    HANDLING(2, "处理中"),
    COMPLETE(3, "已完成"),
    OVER_TIME(4, "超时未完成"),
    CANCEL(5, "任务取消"),
    CANCEL_GRID_DELETE(6, "任务取消(网格删除)"),
	/**
	 * 仅用于作业App查询用，无此状态
	 */
	TRANSFERRED(99, "已转派")
    ;
	
	private WorkTaskStatusEnum(Integer code, String name) {
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
    	WorkTaskStatusEnum data = getEnum(code);
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
    public static WorkTaskStatusEnum getEnum(Integer code) {
        for (WorkTaskStatusEnum value : WorkTaskStatusEnum.values()) {
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
