package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 空铁提货列表状态
 **/
public enum PickingGoodStatusEnum {

    TO_PICKING(0,"待提货"),
    PICKING(1,"提货中"),
    PICKING_COMPLETE(2,"提货完成"),
    ;

    private static final Map<Integer, PickingGoodStatusEnum> codeMap;

    static {
        codeMap = new HashMap<Integer, PickingGoodStatusEnum>();
        for (PickingGoodStatusEnum _enum : PickingGoodStatusEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }

    private Integer code;
    private String name;

    PickingGoodStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }


    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static PickingGoodStatusEnum getEnumByCode(Integer code) {
        return codeMap.get(code);
    }

    public static String getNameByCode(Integer code) {
        PickingGoodStatusEnum _enum = PickingGoodStatusEnum.getEnumByCode(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知";
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
