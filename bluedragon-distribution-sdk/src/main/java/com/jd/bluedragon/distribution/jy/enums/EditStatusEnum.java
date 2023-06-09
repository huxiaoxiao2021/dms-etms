package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName EditStatusEnum
 * @Description 编辑状态,0-未处理,1-处理完成 2-处理中 
 * @Author wyd
 * @Date 2023/5/30 16:49
 **/
public enum EditStatusEnum {
	UNDO(0,"未处理"),
	DONE(1,"处理完成"),
	DOING(2,"处理中")
    ;
	
	private EditStatusEnum(Integer code, String name) {
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
    	EditStatusEnum data = getEnum(code);
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
    public static EditStatusEnum getEnum(Integer code) {
        for (EditStatusEnum value : EditStatusEnum.values()) {
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
