package com.jd.bluedragon.distribution.qualityControl;

/**
 * 异常原因来源
 *
 */
public enum QcVersionFlagEnum {

    NEW_QUALITY_CONTROL_SYSTEM(1, "老质控系统"),

    OLD_QUALITY_CONTROL_SYSTEM(2, "新质控系统");

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

    QcVersionFlagEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static QcVersionFlagEnum getEnum(int type) {
        for (QcVersionFlagEnum status : QcVersionFlagEnum.values()) {
            if (status.getType() == type) {
                return status;
            }
        }
        return null;
    }
}
