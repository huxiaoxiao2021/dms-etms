package com.jd.ql.dms.common.domain;

import com.jd.bluedragon.distribution.transport.domain.ArTransportTypeEnum;

public enum PrintStatusEnum {

    /**
     * 全部
     */
    PRINT_ALL(0, "全部"),
    /**
     * 已打印
     */
    PRINT_YES(1, "已打印"),
    /**
     * 未打印
     */
    PRINT_NO(2, "未打印");

    private int code;

    private String name;

    PrintStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static PrintStatusEnum getEnum(int code) {
        for (PrintStatusEnum type : PrintStatusEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
