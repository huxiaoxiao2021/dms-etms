package com.jd.bluedragon.distribution.jy.enums;

/**
 * 卸车子任务状态
 */
public enum JyBizTaskStageStatusEnum {

    DOING(1,"进行中"),

    COMPLETE(2,"完成");

    private Integer code;
    private String name;

    JyBizTaskStageStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


}
