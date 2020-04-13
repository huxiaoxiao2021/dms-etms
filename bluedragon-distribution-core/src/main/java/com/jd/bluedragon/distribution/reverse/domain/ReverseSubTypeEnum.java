package com.jd.bluedragon.distribution.reverse.domain;
/**
 * 退仓子类型
 * @author wuyoude
 *
 */
public enum ReverseSubTypeEnum {
		/**
		 * 退仓子类型编码
		 * 101-病单拦截
		 * 201-拒收退仓， 202-拦截系统拦截退仓， 203-分拣提交异常退仓的（破损、超区等 ）
		 * 301-预售未付全款
		 */
	    SICK_101(101,ReverseTypeEnum.SICK.getCode(),"病单拦截"),
	    
	    CUSTOMER_201(201,ReverseTypeEnum.CUSTOMER.getCode(),"拒收退仓"),
	    CUSTOMER_202(202,ReverseTypeEnum.CUSTOMER.getCode(),"拦截系统拦截退仓"),
	    CUSTOMER_203(203,ReverseTypeEnum.CUSTOMER.getCode(),"分拣提交异常退仓"),
	    
	    OTHER_301(301,ReverseTypeEnum.OTHER.getCode(),"预售未付全款");
	    
	    private ReverseSubTypeEnum(Integer code, Integer parentCode, String name) {
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
