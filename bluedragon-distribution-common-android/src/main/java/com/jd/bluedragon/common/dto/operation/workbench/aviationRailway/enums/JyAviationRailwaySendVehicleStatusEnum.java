package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 空铁发货列表状态
 * todo 该枚举状态含业务语义，与发货表中状态不对应，需要研发人员自行转换
 **/
public enum JyAviationRailwaySendVehicleStatusEnum {

    TO_SEND(0, "待发货列表"),
    SENDING(1, "发货中列表"),
    SEAL(2, "封车列表"),
    TRUNK_LINE_SEAL_N(21, "干线未封"),
    TRUNK_LINE_SEAL_Y(22, "干线已封"),
    SHUTTLE_SEAL_Y(23, "摆渡已封"),
    ;

    private static final Map<Integer, JyAviationRailwaySendVehicleStatusEnum> codeMap;

    static {
        codeMap = new HashMap<Integer, JyAviationRailwaySendVehicleStatusEnum>();
        for (JyAviationRailwaySendVehicleStatusEnum _enum : JyAviationRailwaySendVehicleStatusEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }

    private Integer code;
    private String name;
    JyAviationRailwaySendVehicleStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        JyAviationRailwaySendVehicleStatusEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知";
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static JyAviationRailwaySendVehicleStatusEnum getEnumByCode(Integer code) {
        return codeMap.get(code);
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
