package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName OperateBizTypeEnum
 * @Description 操作类型
 * @Author wyd
 * @Date 2022/1/4 16:49
 **/
public enum OperateBizTypeEnum {
	INSPECTION("inspection","验货"),
	SORTING("sorting","分拣"),
	SEND_D("send_d","发货明细"),
	BOARD("board","组板"),	
    ;
	
	private OperateBizTypeEnum(String code, String name) {
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
    	OperateBizTypeEnum data = getEnum(code);
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
    public static OperateBizTypeEnum getEnum(String code) {
        for (OperateBizTypeEnum value : OperateBizTypeEnum.values()) {
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
