package com.jd.bluedragon.distribution.reverse.domain;
/**
 * 退仓类型
 * @author wuyoude
 *
 */
public enum GuestBackTypeEnum {
		/**
		 * 退仓类型编码
		 *  0-客退入仓
		 *  1-病单入仓
		 *  2-预售入仓
		 */
		CUSTOMER(0,"客退入仓"),
	    SICK(1,"病单入仓"),
	    PRE_SELL(2,"预售");
	    
	    private GuestBackTypeEnum(Integer code, String name) {
			this.code = code;
			this.name = name;
		}
		private Integer code;
	    
	    private String name;

	    public int getCode() {
	        return code;
	    }
	    public String getName() {
	        return name;
	    }

}
