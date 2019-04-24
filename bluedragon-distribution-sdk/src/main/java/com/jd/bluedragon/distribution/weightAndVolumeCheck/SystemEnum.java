package com.jd.bluedragon.distribution.weightAndVolumeCheck;

/**
 * @ClassName: SystemEnum
 * @Description: 称重计费系统枚举
 * @author: hujiping
 * @date: 2019/4/22 15:24
 */
public enum SystemEnum {
    JIFEI(1, "计费"),
    DMS(2, "分拣"),
    TMS(3, "终端"),
    FXM(4, "FXM"),
    PANZE(5, "判责"),
    ZHIKONG(6, "质控");

    private Integer code;

    private String name;

    SystemEnum(Integer code, String name) {
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
