package com.jd.bluedragon.distribution.station.enums;

/**
 * 场地类型枚举
 *
 * @author hujiping
 * @date 2022/5/7 4:29 PM
 */
public enum SiteTypeEnum {

    FJ_CENTER("1", "分拣中心"),
    ZY_CENTER("2", "转运中心"),
    JHC("3", "接货仓"),
    AIR_RAIL("4", "空铁");

    SiteTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;

    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 根据code获取名称
     * @param code
     * @return
     */
    public static String getNameByCode(String code) {
        SiteTypeEnum data = getEnum(code);
        if(data != null) {
            return data.getName();
        }
        return null;
    }

    /**
     * 根据code获取enum
     * @param code
     * @return
     */
    public static SiteTypeEnum getEnum(String code) {
        for (SiteTypeEnum value : SiteTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
