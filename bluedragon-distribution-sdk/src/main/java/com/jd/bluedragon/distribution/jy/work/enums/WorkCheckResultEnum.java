package com.jd.bluedragon.distribution.jy.work.enums;

/**
 * @ClassName WorkCheckResultEnum
 * @Description 检查结果,0-未选择,1-符合 2-不符合
 * @Author wyd
 * @Date 2023/5/30 16:49
 **/
public enum WorkCheckResultEnum {
	UNDO(0,"未处理"),
	PASS (1, "符合"),
    UNPASS(2, "不符合")
    ;
	
	private WorkCheckResultEnum(Integer code, String name) {
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
    	WorkCheckResultEnum data = getEnum(code);
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
    public static WorkCheckResultEnum getEnum(Integer code) {
        for (WorkCheckResultEnum value : WorkCheckResultEnum.values()) {
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
