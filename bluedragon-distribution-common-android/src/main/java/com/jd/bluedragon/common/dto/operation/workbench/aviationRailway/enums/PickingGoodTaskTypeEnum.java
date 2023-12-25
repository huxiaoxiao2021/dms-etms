package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

import java.util.HashMap;
import java.util.Map;

public enum PickingGoodTaskTypeEnum {

    AVIATION(1,"航空提货任务"),
    RAILWAY(2,"铁路提货任务"),
//    VEHICLE(3,"发车任务"),
    ;
    private Integer code;
    private String name;

    private static final Map<Integer, PickingGoodTaskTypeEnum> codeMap;

    static {
        codeMap = new HashMap<Integer, PickingGoodTaskTypeEnum>();
        for (PickingGoodTaskTypeEnum _enum : PickingGoodTaskTypeEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }
    PickingGoodTaskTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean legalCheck(Integer code) {
        if(PickingGoodTaskTypeEnum.AVIATION.code.equals(code) || PickingGoodTaskTypeEnum.RAILWAY.code.equals(code)) {
            return true;
        }
        return false;
    }

    public static String getNameByCode(Integer code) {
        if(null == code) {
            return null;
        }
        PickingGoodTaskTypeEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "提货任务";
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
