package com.jd.bluedragon.distribution.station.enums;

/**
 * @ClassName SignFlowTypeEnum
 * @Description 签到流程状态
 * @Author wyd
 * @Date 2023/03/10 16:49
 **/
public enum SignFlowStatusEnum {
	DEFALUT(0,"初始"),
	PENDING_APPROVAL(10,"待审批"),
	ADD_COMPLETE(21,"补签完成"),
	MODIFY_COMPLETE(22,"修改完成"),
	DELETE_COMPLETE(23,"作废完成"),
	REFUSE(2,"驳回"),
	REJECT(3,"驳回"),
	CANCEL(4,"取消"),
	COMPLETE_REJECT(6,"驳回"),
	COMPLETE_CANCEL(7,"取消"),
	COMPLETE_SKIP(8,"流程结束(未审批)"),
	ABNORMAL(9,"异常"),
	FAIL(10,"失败")
    ;
	
	private SignFlowStatusEnum(Integer code, String name) {
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
    	SignFlowStatusEnum data = getEnum(code);
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
    public static SignFlowStatusEnum getEnum(Integer code) {
        for (SignFlowStatusEnum value : SignFlowStatusEnum.values()) {
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
