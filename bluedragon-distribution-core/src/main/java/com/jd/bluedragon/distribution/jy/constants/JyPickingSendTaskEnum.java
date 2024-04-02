package com.jd.bluedragon.distribution.jy.constants;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.JyPickingSendBatchCodeStatusEnum;

public enum JyPickingSendTaskEnum {

    TO_SEND(JyPickingSendBatchCodeStatusEnum.TO_SEND.getCode(), "待发货"),
    SENDING(JyPickingSendBatchCodeStatusEnum.SENDING.getCode(), "发货中"),
    TO_SEAL(JyPickingSendBatchCodeStatusEnum.TO_SEAL.getCode(), "待封车"),
    SEALED(JyPickingSendBatchCodeStatusEnum.SEALED.getCode(),"已封车"),
    ;
    private Integer code;
    private String name;
    JyPickingSendTaskEnum(Integer code, String name) {
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
