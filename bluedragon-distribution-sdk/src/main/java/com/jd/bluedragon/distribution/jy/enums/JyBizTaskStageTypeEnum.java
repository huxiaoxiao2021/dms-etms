package com.jd.bluedragon.distribution.jy.enums;

/**
 * 卸车子任务模式
 */
public enum JyBizTaskStageTypeEnum {

    SUPPLEMENT(1,"补扫"),

    HANDOVER(2,"交接班");

    private Integer code;
    private String name;

    JyBizTaskStageTypeEnum(Integer code, String name) {
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
