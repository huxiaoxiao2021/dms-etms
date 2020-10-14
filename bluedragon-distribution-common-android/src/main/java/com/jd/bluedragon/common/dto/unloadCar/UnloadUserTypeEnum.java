package com.jd.bluedragon.common.dto.unloadCar;

/**
 * @author lijie
 * @date 2020/7/3 1:41
 */
public enum UnloadUserTypeEnum {

    UNLOAD_MASTER(0,"卸车负责人"),

    HELPER(1,"卸车协助人");

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

    UnloadUserTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static UnloadUserTypeEnum getEnum(int type) {
        for (UnloadUserTypeEnum typeEnum : UnloadUserTypeEnum.values()) {
            if (typeEnum.getType() == type) {
                return typeEnum;
            }
        }
        return null;
    }

}
