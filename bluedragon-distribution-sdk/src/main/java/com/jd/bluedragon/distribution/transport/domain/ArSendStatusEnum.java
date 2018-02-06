package com.jd.bluedragon.distribution.transport.domain;

/**
 * <p>
 * Created by lixin39 on 2018/1/4.
 */
public enum ArSendStatusEnum {

    ALREADY_SEND(1, "已发货"),

    ALREADY_DELIVERED(2, "已提货");

    private int type;

    private String name;

    ArSendStatusEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static ArSendStatusEnum getEnum(int type) {
        for (ArSendStatusEnum status : ArSendStatusEnum.values()) {
            if (status.getType() == type) {
                return status;
            }
        }
        return null;
    }

}
