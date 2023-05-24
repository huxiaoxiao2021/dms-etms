package com.jd.bluedragon.distribution.weightVolume.enums;

/**
 * @ClassName OverLengthAndWeightTypeEnum
 * @Description 超长超重服务类型
 * @Author wyd
 * @Date 2023/05/4 16:49
 **/
public enum OverLengthAndWeightTypeEnum {
	//cm
	ONE_SIDE("oneSide","3.1m<单边<=4m",310,400),
	THREED_SIDE("threeSide","5.5m<三边和<=6.3m",550,630),
	OVER_WEIGHT("overWeight","300kg<单件<=1000kg",300,1000)
    ;
	
	private OverLengthAndWeightTypeEnum(String code, String name, double minVal, double maxVal) {
		this.code = code;
		this.name = name;
		this.minVal = minVal;
		this.maxVal = maxVal;
	}

	private String code;
	private String name;
	private double minVal;
	private double maxVal;
	
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
    /**
     * 判断是否匹配
     * @param val
     * @return
     */
    public boolean isMatch(Double val) {
    	if(val == null) {
    		return false;
    	}
        return val > minVal && val <= maxVal;
    }
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
	public double getMinVal() {
		return minVal;
	}
	public double getMaxVal() {
		return maxVal;
	}
}
