package com.jd.bluedragon.common.dto.blockcar.enumeration;

/**
 * 传摆封车场景枚举
 *
 * @author: hujiping
 * @date: 2020/3/22 18:17
 */
public enum FerrySealCarSceneEnum {

    PARK_SEAL_CAR(10,"园内用车"),

    AIRLINE_SEAL_CAR(20,"空铁提货用车"),

    WMS_SEAL_CAR(20,"退货到仓用车");

    private Integer code;

    private String name;

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
    FerrySealCarSceneEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }
    public static FerrySealCarSceneEnum getEnum(Integer code) {
        for (FerrySealCarSceneEnum type : FerrySealCarSceneEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
