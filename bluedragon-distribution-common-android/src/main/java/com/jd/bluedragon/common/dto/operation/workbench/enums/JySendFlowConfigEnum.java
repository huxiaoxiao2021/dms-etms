package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @author liwenji
 * @description 关注
 * @date 2023-05-18 11:26
 */
public enum JySendFlowConfigEnum {
    GANTRY(101,"按龙门架配置发货"),
    ROUTER(201,"按路由配置发货"),
    ;
    private int code;
    private String desc;

    JySendFlowConfigEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
