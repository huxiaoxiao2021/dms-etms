package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liwenji
 * @description 发货异常类型枚举
 * @date 2023-08-18 14:51
 */
public enum SendAbnormalTypeEnum {
    INTERCEPT(1,"拦截"),
    FORCE_SEND(2,"强发");
    private Integer code;
    private String name;

    SendAbnormalTypeEnum(Integer code, String name) {
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
