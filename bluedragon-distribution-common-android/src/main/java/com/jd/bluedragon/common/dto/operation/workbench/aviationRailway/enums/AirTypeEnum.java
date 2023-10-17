package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

import java.util.HashMap;
import java.util.Map;

public enum AirTypeEnum {

    AIR_TYPE_BULK(1,"散航"),
    AIR_TYPE_ALL(2,"全货"),
    ;
    private Integer code;
    private String name;

    private static final Map<Integer, AirTypeEnum> codeMap;

    static {
        codeMap = new HashMap<Integer, AirTypeEnum>();
        for (AirTypeEnum _enum : AirTypeEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }
    public static String getNameByCode(Integer code) {
        AirTypeEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知";
    }

    AirTypeEnum(Integer code, String name) {
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
