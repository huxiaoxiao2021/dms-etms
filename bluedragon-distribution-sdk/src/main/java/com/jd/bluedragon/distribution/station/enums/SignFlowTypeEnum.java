package com.jd.bluedragon.distribution.station.enums;

/**
 * @ClassName SignFlowTypeEnum
 * @Description 签到流程操作类型
 * @Author wyd
 * @Date 2023/03/10 16:49
 **/
public enum SignFlowTypeEnum {
	ADD(1,"补签"),
	MODIFY(2,"修改"),
	DELETE(3,"作废"),
    ;
	
	private SignFlowTypeEnum(Integer code, String name) {
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
    	SignFlowTypeEnum data = getEnum(code);
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
    public static SignFlowTypeEnum getEnum(Integer code) {
        for (SignFlowTypeEnum value : SignFlowTypeEnum.values()) {
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
