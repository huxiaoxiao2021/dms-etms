package com.jd.bluedragon.distribution.jy.enums;

public enum BoxTransportTypeEnum {
    AVIATION(1, "航空运输"),
    HIGHWAY(2, "公路运输"),
    RAILWAY(3,"铁路运输"),
    CITY_FOUR_HOUR(4,"同城四小时")
    ;

    private Integer code;

    private String name;

    BoxTransportTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(Integer code) {
        for (BoxTransportTypeEnum item : BoxTransportTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item.name;
            }
        }
        return "";
    }
}
