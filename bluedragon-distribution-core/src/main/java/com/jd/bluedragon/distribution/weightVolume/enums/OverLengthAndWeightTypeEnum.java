package com.jd.bluedragon.distribution.weightVolume.enums;

/**
 * @ClassName OverLengthAndWeightTypeEnum
 * @Description 超长超重服务类型
 * @Author wyd
 * @Date 2023/05/4 16:49
 **/
public enum OverLengthAndWeightTypeEnum {
	ONE("1","3.1m<单边<4m"),
	OVER2("2","5.5m<三边和<=6.3m"),
	OVER3("3","300kg<单件<1000kg")
    ;
	
	private OverLengthAndWeightTypeEnum(String code, String name) {
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
    	OverLengthAndWeightTypeEnum data = getEnum(code);
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
    public static OverLengthAndWeightTypeEnum getEnum(String code) {
        for (OverLengthAndWeightTypeEnum value : OverLengthAndWeightTypeEnum.values()) {
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
