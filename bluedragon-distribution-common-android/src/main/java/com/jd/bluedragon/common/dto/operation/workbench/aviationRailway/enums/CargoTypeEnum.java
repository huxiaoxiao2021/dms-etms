package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

import java.util.HashMap;
import java.util.Map;

public enum CargoTypeEnum {

    COMMON(1,"普货"),
    FRESH(2,"生鲜"),
    SPECIAL_A(3,"特货A"),
    SPECIAL_B(4,"特货B"),
    SPECIAL_C(5,"特货C"),
    ;
    private Integer code;
    private String name;
    private static final Map<Integer, CargoTypeEnum> codeMap;

    static {
        codeMap = new HashMap<Integer, CargoTypeEnum>();
        for (CargoTypeEnum _enum : CargoTypeEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }

    CargoTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        CargoTypeEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知";
    }

    /**
     * 是否普货或生鲜
     * @param code
     * @return
     */
    public static boolean isCommonOrFresh(Integer code) {
        if(CargoTypeEnum.COMMON.getCode().equals(code) || CargoTypeEnum.FRESH.getCode().equals(code)) {
            return true;
        }
        return false;
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
