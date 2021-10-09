package com.jd.bluedragon.distribution.spotcheck.enums;

/**
 * 抽检维度枚举
 *
 * @author hujiping
 * @date 2021/8/18 4:04 下午
 */
public enum SpotCheckDimensionEnum {

    SPOT_CHECK_WAYBILL(1,"运单维度抽检"),
    SPOT_CHECK_PACK(0,"包裹维度抽检");

    private Integer code;
    private String name;

    SpotCheckDimensionEnum(Integer code, String name) {
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
