package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

/**
 * @Author zhengchengfa
 * @Date 2024/4/2 14:52
 * @Description
 */
public enum JyPickingSendBatchCodeStatusEnum {

    TO_SEND(0, "待发货"),
    SENDING(1, "发货中"),
    TO_SEAL(2, "待封车"),
    SEALED(3,"已封车"),
            ;
    private Integer code;
    private String name;
    JyPickingSendBatchCodeStatusEnum(Integer code, String name) {
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
