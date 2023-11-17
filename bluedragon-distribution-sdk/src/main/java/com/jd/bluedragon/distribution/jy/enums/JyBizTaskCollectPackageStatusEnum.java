package com.jd.bluedragon.distribution.jy.enums;

public enum JyBizTaskCollectPackageStatusEnum {
    TO_COLLECT(1, "待集包"),
    COLLECTING(2, "集包中"),
    SEALED(3, "已封箱"),
    CANCEL(4, "已作废");

    private Integer code;
    private String name;

    JyBizTaskCollectPackageStatusEnum(Integer code, String name) {
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
