package com.jd.bluedragon.distribution.jy.constants;

public enum JyPickingSendTaskEnum {

    TO_SEND(0, "待发货"),
    SENDING(1, "发货中"),
    TO_SEAL(2, "待封车"),
    SEALED(3,"已封车"),
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
