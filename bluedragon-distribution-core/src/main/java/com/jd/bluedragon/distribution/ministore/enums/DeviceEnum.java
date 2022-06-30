package com.jd.bluedragon.distribution.ministore.enums;

public enum DeviceEnum {

    MINI_STORE(1,"微仓/保温箱"),
    INCE_BOARD(2,"冰板"),
    BOX(3,"箱号/集包");
    DeviceEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private Integer type;
    private String desc;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
