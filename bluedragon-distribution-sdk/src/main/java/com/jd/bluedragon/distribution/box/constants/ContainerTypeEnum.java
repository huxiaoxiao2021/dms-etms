package com.jd.bluedragon.distribution.box.constants;

public enum ContainerTypeEnum {
    BATCH(1,"批次"),
    BOARD(2,"板"),
    CAGE_CAR(3,"笼车"),
    TURN_OVER_BOX(4,"周转箱"),
    NORMAL_BOX(5,"普通箱");

    private Integer code;
    private String name;

    ContainerTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
