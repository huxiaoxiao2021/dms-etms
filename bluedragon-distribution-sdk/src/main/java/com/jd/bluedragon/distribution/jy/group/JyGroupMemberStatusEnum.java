package com.jd.bluedragon.distribution.jy.group;

/**
 * @ClassName 小组人员状态
 * @Description 工种类型
 * @Author wyd
 * @Date 2022/4/2 16:49
 **/
public enum JyGroupMemberStatusEnum {
	OUT(0,"退出"),
	IN(1,"正常")
    ;
	
	private JyGroupMemberStatusEnum(Integer code, String name) {
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
    	JyGroupMemberStatusEnum data = getEnum(code);
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
    public static JyGroupMemberStatusEnum getEnum(Integer code) {
        for (JyGroupMemberStatusEnum value : JyGroupMemberStatusEnum.values()) {
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
