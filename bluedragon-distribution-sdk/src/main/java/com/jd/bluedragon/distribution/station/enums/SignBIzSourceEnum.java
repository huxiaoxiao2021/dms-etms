package com.jd.bluedragon.distribution.station.enums;

/**
 * @ClassName SignBIzSourceEnum
 * @Description 签到流程操作来源
 * @Author wyd
 * @Date 2023/03/10 16:49
 **/
public enum SignBIzSourceEnum {
	PDA(1,"PDA端"),
	PC(2,"PC端"),
    ;
	
	private SignBIzSourceEnum(Integer code, String name) {
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
    	SignBIzSourceEnum data = getEnum(code);
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
    public static SignBIzSourceEnum getEnum(Integer code) {
        for (SignBIzSourceEnum value : SignBIzSourceEnum.values()) {
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
