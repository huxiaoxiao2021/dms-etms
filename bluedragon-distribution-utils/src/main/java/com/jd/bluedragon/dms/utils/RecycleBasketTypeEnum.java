package com.jd.bluedragon.dms.utils;

/**
 * @author liwenji
 * @description nc周转筐类型
 * @date 2023-08-11 11:11
 */
public enum RecycleBasketTypeEnum {

    SMALL(3, "1号周转筐（小型）"),
    BIG(4, "2号周转筐（大型）")
    ;

    private Integer code;

    private String name;

    RecycleBasketTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


    public static RecycleBasketTypeEnum getFromCode(int code) {
        for (RecycleBasketTypeEnum item : RecycleBasketTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
    
}
