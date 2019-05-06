package com.jd.bluedragon.distribution.print.domain;
/**
 * 
 * @ClassName: TemplateGroupEnum
 * @Description: 模板分组
 * @author: wuyoude
 * @date: 2019年4月22日 下午5:42:08
 *
 */
public enum TemplateGroupEnum {
	/**
	 * B网面单
	 */
	TEMPLATE_GROUP_B("B","B网面单"),
	/**
	 * C网面单
	 */
	TEMPLATE_GROUP_C("C","C网面单"),
	/**
	 * TC网面单
	 */
	TEMPLATE_GROUP_TC("TC","TC面单");
	/**
	 * 编码
	 */
    private String code;
	/**
	 * 名称
	 */
    private String name;
    
    public static final String TEMPLATE_GROUP_CODE_B = TEMPLATE_GROUP_B.code;
    public static final String TEMPLATE_GROUP_CODE_C = TEMPLATE_GROUP_C.code;
    public static final String TEMPLATE_GROUP_CODE_TC = TEMPLATE_GROUP_TC.code;
	
	private TemplateGroupEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}
	/**
	 * 
	 * @param code
	 * @return
	 */
	public static TemplateGroupEnum valueof(String code){
		if(TEMPLATE_GROUP_CODE_B.equals(code)){
			return TEMPLATE_GROUP_B;
		}else if (TEMPLATE_GROUP_CODE_C.equals(code)){
			return TEMPLATE_GROUP_C;
		}else if (TEMPLATE_GROUP_CODE_TC.equals(code)){
			return TEMPLATE_GROUP_TC;
		}
		return null;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
