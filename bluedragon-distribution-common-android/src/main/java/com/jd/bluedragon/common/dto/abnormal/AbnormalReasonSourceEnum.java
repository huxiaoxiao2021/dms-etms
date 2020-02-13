package com.jd.bluedragon.common.dto.abnormal;

/**
 * 异常原因来源
 *
 */
public enum AbnormalReasonSourceEnum {

    QUALITY_CONTROL_SYSTEM(1, "质控系统"),

    BASIC_SYSTEM(2, "基础资料");

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

    AbnormalReasonSourceEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static AbnormalReasonSourceEnum getEnum(int type) {
        for (AbnormalReasonSourceEnum status : AbnormalReasonSourceEnum.values()) {
            if (status.getType() == type) {
                return status;
            }
        }
        return null;
    }
}
