package com.jd.bluedragon.enums;

/**
 * @program: tys-android
 * @description:
 * @author: wuming
 * @create: 2021-03-01 16:56
 */
public enum WageBusinessEnum {

    LOAD_CAR(15,"装车"),
    UNLOAD_CAR(13, "卸车"),
    FORK_CAR(16, "叉车"),
    LOAD_CAR_SCAN(25, "装车扫描"),
    UNLOAD_CAR_SCAN(26, "卸车扫描"),
    UNLOAD_CAR_BOARD(50, "卸车组板");


    private Integer code;
    private String desc;

    WageBusinessEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
