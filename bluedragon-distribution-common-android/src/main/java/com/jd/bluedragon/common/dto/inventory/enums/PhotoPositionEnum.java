package com.jd.bluedragon.common.dto.inventory.enums;

/**
 * 处理（责任）部门类型
 *
 */
public enum PhotoPositionEnum {

    NORTH(1, "北"),
    EAST(2, "东"),
    SOUTH(3, "南"),
    WEST(4, "西"),
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

    public static final boolean isLegal(Integer code) {
        if(code == null) {
            return false;
        }
        for (PhotoPositionEnum en : PhotoPositionEnum.values()) {
            if(en.getCode() == code) {
                return true;
            }
        }
        return false;
    }

}
