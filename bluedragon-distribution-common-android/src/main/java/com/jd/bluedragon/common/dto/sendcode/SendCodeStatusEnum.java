package com.jd.bluedragon.common.dto.sendcode;

/**
 * @ClassName SendCodeStatusEnum
 * @Description 批次状态
 * @Author wyd
 * @Date 2022/10/12 16:49
 **/
public enum SendCodeStatusEnum {
	UNSEAL(1,"未封车"),
	SEALED(2,"已封车")
    ;
	
	private SendCodeStatusEnum(Integer code, String name) {
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
    	SendCodeStatusEnum data = getEnum(code);
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
    public static SendCodeStatusEnum getEnum(Integer code) {
        for (SendCodeStatusEnum value : SendCodeStatusEnum.values()) {
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
