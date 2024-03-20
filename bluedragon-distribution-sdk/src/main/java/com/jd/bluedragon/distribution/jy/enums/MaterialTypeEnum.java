package com.jd.bluedragon.distribution.jy.enums;

public enum MaterialTypeEnum {
    M_CYCLE_BAG(1,"集包袋"),M_CAGE_CAR(2,"笼车"),M_PALLET_BOX(3,"围板箱");

    private Integer code;
    private String name;

    MaterialTypeEnum(Integer code, String name) {
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
