package com.jd.bluedragon.distribution.reverse.domain;
/**
 * 退仓类型
 * @author wuyoude
 *
 */
public enum ReverseTypeEnum {
		/**
		 * 退仓类型编码
		 *  1-病单入仓
		 *  2-客退入仓
		 *  3-其他
		 */
	    SICK(1,"病单入仓"),
	    CUSTOMER(2,"客退入仓"),
	    OTHER(3,"其他");
	    
	    private ReverseTypeEnum(Integer code, String name) {
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
