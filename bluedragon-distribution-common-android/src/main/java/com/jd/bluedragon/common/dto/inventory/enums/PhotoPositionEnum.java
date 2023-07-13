package com.jd.bluedragon.common.dto.inventory.enums;

/**
 * 处理（责任）部门类型
 *
 */
public enum PhotoPositionEnum {

    NORTH(1, "北"),
    NORTHEAST(2, "东北"),
    EAST(3, "东"),
    SOUTHEAST(4, "东南"),
    SOUTH(5, "南"),
    SOUTHWEST(6, "西南"),
    WEST(7, "西"),
    NORTHWEST(8, "西北"),
    ;

    private int code;

    private String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    PhotoPositionEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
