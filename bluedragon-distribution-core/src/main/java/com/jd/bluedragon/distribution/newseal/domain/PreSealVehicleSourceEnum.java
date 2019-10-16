package com.jd.bluedragon.distribution.newseal.domain;

public enum PreSealVehicleSourceEnum {

    COMMON_PRE_SEAL(1,"普通预封车"),
    FERRY_PRE_SEAL(2,"传摆预封车");

    private Integer code;

    private String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    PreSealVehicleSourceEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public static PreSealVehicleSourceEnum getEnum(Integer code) {
        for (PreSealVehicleSourceEnum type : PreSealVehicleSourceEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
