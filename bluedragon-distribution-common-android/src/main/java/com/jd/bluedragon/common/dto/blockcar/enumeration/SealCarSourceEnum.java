package com.jd.bluedragon.common.dto.blockcar.enumeration;

/**
 * 封车来源枚举
 *
 * @author: hujiping
 * @date: 2020/3/19 22:29
 */
public enum SealCarSourceEnum {

    COMMON_SEAL_CAR(10,"普通封车"),

    FERRY_SEAL_CAR(20,"传摆封车");

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
    SealCarSourceEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }
    public static SealCarSourceEnum getEnum(Integer code) {
        for (SealCarSourceEnum type : SealCarSourceEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
