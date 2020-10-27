package com.jd.bluedragon.distribution.reverse.domain;
/**
 * 退仓子类型
 * @author wuyoude
 *
 */
public enum GuestBackSubTypeEnum {
		/**
		 * 退仓子类型编码
		 * 101-拒收退仓， 102-拦截系统拦截退仓， 103-分拣提交异常退仓的（破损、超区等 ）
		 * 201-病单拦截
		 * 301-预售未付全款
		 */
	    CUSTOMER_01(101,GuestBackTypeEnum.CUSTOMER.getCode(),"拒收退仓"),
	    CUSTOMER_02(102,GuestBackTypeEnum.CUSTOMER.getCode(),"拦截系统拦截退仓"),
	    CUSTOMER_03(103,GuestBackTypeEnum.CUSTOMER.getCode(),"分拣提交异常退仓"),
	    
	    SICK_01(201,GuestBackTypeEnum.SICK.getCode(),"病单拦截"),
	    
	    PRE_SELL_01(301,GuestBackTypeEnum.PRE_SELL.getCode(),"预售未付全款");
	    
	    private GuestBackSubTypeEnum(Integer code, Integer parentCode, String name) {
			this.code = code;
			this.parentCode = parentCode;
			this.name = name;
		}
		private Integer code;
	    
	    private Integer parentCode;

	    private String name;

		public Integer getCode() {
			return code;
		}

		public Integer getParentCode() {
			return parentCode;
		}

		public String getName() {
			return name;
		}
}
