package com.jd.bluedragon.distribution.weightAndVolumeCheck;

/**
 * 责任类型
 *
 * @author: hujiping
 * @date: 2019/10/31 17:56
 */
public enum DutyTypeEnum {

    WMS(1, "仓"),
    DMS(2, "分拣"),
    SITE(3, "站点"),
    BIZ(4, "商家"),
    OTHER(5, "其它"),
    FLEET(6, "车队");

    private Integer code;

    private String name;

    DutyTypeEnum(Integer code, String name) {
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
