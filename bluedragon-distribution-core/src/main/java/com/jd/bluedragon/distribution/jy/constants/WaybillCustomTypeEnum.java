package com.jd.bluedragon.distribution.jy.constants;

import org.apache.logging.log4j.util.Strings;

public enum WaybillCustomTypeEnum {
    DEFAULT(Strings.EMPTY, "未知"),
    TO_B("toB", "B网"),
    TO_C("toC", "C网")
    ;
    private String code;
    private String desc;

    WaybillCustomTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
