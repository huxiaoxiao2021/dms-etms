package com.jd.bluedragon.distribution.spotcheck.enums;

/**
 * 抽检业务类型枚举
 *
 * @author hujiping
 * @date 2021/8/10 2:29 下午
 */
public enum SpotCheckBusinessTypeEnum {

    SPOT_CHECK_TYPE_C(0,"C网"),
    SPOT_CHECK_TYPE_B(1,"B网"),
    SPOT_CHECK_TYPE_MEDICAL(2,"医药"),
    SPOT_CHECK_TYPE_X(-1,"未知");

    private Integer code;
    private String name;

    SpotCheckBusinessTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String analysisNameFromCode(int code){
        for (SpotCheckBusinessTypeEnum value : SpotCheckBusinessTypeEnum.values()) {
            if(value.getCode() == code){
                return value.getName();
            }
        }
        return null;
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
