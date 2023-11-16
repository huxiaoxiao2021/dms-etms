package com.jd.bluedragon.enums;

import lombok.Getter;

/**
 * 运单类型枚举
 */
@Getter
public enum WaybillFlowTypeEnum {

    HK_OR_MO(0,"港澳单"),
    INTERNATION(1,"国际单"),
    MAINLAND(2,"大陆单");

    private Integer code;
    private String name;

    WaybillFlowTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

}
