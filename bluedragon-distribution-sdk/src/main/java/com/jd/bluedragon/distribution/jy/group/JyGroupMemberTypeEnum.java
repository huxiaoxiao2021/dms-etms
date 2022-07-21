package com.jd.bluedragon.distribution.jy.group;

/**
 * @ClassName JyGroupMemberTypeEnum
 * @Description 小组成员类型
 * @Author wyd
 * @Date 2022/7/2 16:49
 **/
public enum JyGroupMemberTypeEnum {
	PERSON(1,"人员"),
	DEVICE(2,"设备")
    ;
	
	private JyGroupMemberTypeEnum(Integer code, String name) {
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
    	JyGroupMemberTypeEnum data = getEnum(code);
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
    public static JyGroupMemberTypeEnum getEnum(Integer code) {
        for (JyGroupMemberTypeEnum value : JyGroupMemberTypeEnum.values()) {
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
