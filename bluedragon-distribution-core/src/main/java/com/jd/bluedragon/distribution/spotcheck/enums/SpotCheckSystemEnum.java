package com.jd.bluedragon.distribution.spotcheck.enums;

/**
 *  抽检系统枚举
 *
 * @author hujiping
 * @date 2021/9/3 2:57 下午
 */
public enum SpotCheckSystemEnum {

    JIFEI(1, "计费"),
    DMS(2, "分拣"),
    TMS(3, "终端"),
    FXM(4, "FXM"),
    PANZE(5, "判责"),
    ZHIKONG(6, "质控");

    private Integer code;

    private String name;

    SpotCheckSystemEnum(Integer code, String name) {
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
