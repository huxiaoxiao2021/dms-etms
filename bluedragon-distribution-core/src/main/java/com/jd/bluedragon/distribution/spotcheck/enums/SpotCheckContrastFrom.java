package com.jd.bluedragon.distribution.spotcheck.enums;

/**
 * 抽检核对来源
 *
 * @author hujiping
 * @date 2021/12/6 3:42 下午
 */
public enum SpotCheckContrastFrom {

    SOURCE_FROM_BILLING(1, "计费"),
    SOURCE_FROM_WAYBILL(2, "运单"),
    SOURCE_FROM_ORDER(3, "下单");

    private Integer code;
    private String name;

    SpotCheckContrastFrom(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
