package com.jd.bluedragon.distribution.box.domain;

import java.util.Map;
import java.util.TreeMap;

/**
 * 箱子归属系统类型枚举值
 *
 * 01-打印客户端生成箱号 02-自动分拣机箱号
 */
public enum BoxSystemTypeEnum {

    PRINT_CLIENT("01", "打印客户端"),
    AUTO_SORTING_MACHINE("02", "自动分拣机");

    private String code;
    private String name;

    BoxSystemTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.getCode() + "-" + this.getName();
    }

}
