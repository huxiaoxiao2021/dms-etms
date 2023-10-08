package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName OperateBizSubTypeEnum
 * @Description 操作子类型
 * @Author wyd
 * @Date 2022/1/4 16:49
 **/
public enum OperateBizSubTypeEnum {
	INSPECTION("inspection","验货"),
	SORTING("sorting","分拣"),
	SORTING_CANCEL("sorting_cancel","取消分拣"),
	SEND("send_d","发货"),
	SEND_CANCEL("send_d_cancel","取消发货"),
	BOARD("board","组板"),
	BOARD_CANCEL("board_cancel","取消组板"),
    ;
	
	private OperateBizSubTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	private String code;
	private String name;
	/**
	 * 根据code获取名称
	 * @param code
	 * @return
	 */
    public static String getNameByCode(String code) {
    	OperateBizSubTypeEnum data = getEnum(code);
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
    public static OperateBizSubTypeEnum getEnum(String code) {
        for (OperateBizSubTypeEnum value : OperateBizSubTypeEnum.values()) {
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
    public boolean exist(String code) {
        return null != getEnum(code);
    }

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
