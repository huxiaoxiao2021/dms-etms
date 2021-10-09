package com.jd.bluedragon.distribution.spotcheck.enums;

/**
 * 抽检核对来源枚举
 *
 * @author hujiping
 * @date 2021/8/10 2:29 下午
 */
public enum ContrastSourceFromEnum {

    SOURCE_FROM_BILLING(0, "计费"),
    SOURCE_FROM_WAYBILL(1, "运单");

    private Integer code;
    private String name;

    ContrastSourceFromEnum(Integer code, String name) {
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
