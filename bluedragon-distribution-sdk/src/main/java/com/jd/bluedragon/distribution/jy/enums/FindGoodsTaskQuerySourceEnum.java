package com.jd.bluedragon.distribution.jy.enums;

/**
 * 清场找货 任务查询来源枚举
 */
public enum FindGoodsTaskQuerySourceEnum {
    TIMER_QUERY(1,"定时查询"),
    USER_QUERY(2,"用户主动查询");
    private Integer code;
    private String name;

    FindGoodsTaskQuerySourceEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
