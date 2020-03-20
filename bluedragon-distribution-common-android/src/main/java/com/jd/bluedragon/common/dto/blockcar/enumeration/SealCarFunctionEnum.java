package com.jd.bluedragon.common.dto.blockcar.enumeration;

/**
 * 封车功能枚举
 *
 * @author: hujiping
 * @date: 2020/3/19 22:44
 */
public enum SealCarFunctionEnum {

    SEAL_CAR(10,"封车"),

    CANCEL_SEAL_CAR(20,"取消封车");

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
    SealCarFunctionEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }
    public static SealCarFunctionEnum getEnum(Integer code) {
        for (SealCarFunctionEnum type : SealCarFunctionEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
