package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 *
 * 1-散航，2-全货机，3-铁路
 *
 */
public enum BookingTypeEnum {

    BULK_AIRCRAFT(1,"散航"),
    ALL_CARGO_AIRCRAFT(2,"全货机"),
//    RAILWAY("3","铁路");
    ;
    private Integer code;
    private String name;

    BookingTypeEnum(Integer code, String name) {
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
