package com.jd.bluedragon.common.dto.ministore;

/**
 * 微仓状态扭转枚举
 */
public enum MiniStoreProcessStatusEnum {
    UNKNOWN("0","未知"),
    BIND("1","绑定"),
    UNBIND("2","解绑"),
    SORT("3","集包"),
    SEAL_BOX("4","封箱"),
    UN_SEAL_BOX("5","解封箱"),
    DELIVER_GOODS("6","发货（接货仓）"),
    CHECK_GOODS("7","验货"),
    SORTCENTER_DELIVER_GOODS("8","发货（分拣中心）"),
    BACK_TO_STORE("9","逆向回仓");

    private String code;
    private String msg;

    MiniStoreProcessStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static MiniStoreProcessStatusEnum codeOf(String code) {
        for (MiniStoreProcessStatusEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return UNKNOWN;
    }
}
