package com.jd.bluedragon.distribution.popPrint.domain;

/**
 * 驻厂类型
 *
 * @author: hujiping
 * @date: 2020/10/23 10:29
 */
public enum ResidentTypeEnum {

    RESIDENT_DESK_CLIENT(1, "客户端驻厂"),

    RESIDENT_GANTRY(2, "龙门架驻厂");

    private int type;

    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    ResidentTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static ResidentTypeEnum getEnum(int type) {
        for (ResidentTypeEnum status : ResidentTypeEnum.values()) {
            if (status.getType() == type) {
                return status;
            }
        }
        return null;
    }

}
