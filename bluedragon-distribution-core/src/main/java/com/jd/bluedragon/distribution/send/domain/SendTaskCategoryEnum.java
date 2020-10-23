package com.jd.bluedragon.distribution.send.domain;

/**
 * @author wuyoude
 * @Description 发货任务
 * @ClassName SendTaskCategoryEnum
 * @date 2020/10/15
 */
public enum SendTaskCategoryEnum {
	/**
	 * 1-批次发货
	 */
	BATCH_SEND(1, "批次发货","batchSend"),
	/**
	 * 2-箱号发货
	 */
	BOX_SEND(2, "箱号发货","boxSend"),
	/**
	 * 3-箱号中转发货
	 */
	BOX_TRANSIT_SEND(3, "箱号中转发货","boxTransitSend"),
	/**
	 * 4-包裹发货
	 */
    PACKAGE_SEND(4, "包裹发货","packageSend");
    /**
     * 编码
     */
    private Integer code;
    /**
     * 名称
     */
    private String name;
    /**
     * 标识
     */
    private String key;
    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 构造方法
     *
     * @param code 编码
     * @param name 名称
     */
    SendTaskCategoryEnum(Integer code, String name ,String key) {
        this.code = code;
        this.name = name;
        this.key = key;
    }

    public static SendTaskCategoryEnum getEnum(Integer code) {
        if (code != null) {
            for (SendTaskCategoryEnum bizSource : SendTaskCategoryEnum.values()) {
                if (bizSource.getCode().equals(code)) {
                    return bizSource;
                }
            }
        }
        return null;
    }

	public String getKey() {
		return key;
	}
}
