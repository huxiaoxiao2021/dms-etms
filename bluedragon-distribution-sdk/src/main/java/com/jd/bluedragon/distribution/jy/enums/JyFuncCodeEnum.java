package com.jd.bluedragon.distribution.jy.enums;

import java.util.HashMap;
import java.util.Map;

public enum JyFuncCodeEnum {

    UNSEAL_CAR_POSITION("UNSEAL_CAR_POSITION", "拣运到车岗"),
    UNLOAD_CAR_POSITION("UNLOAD_CAR_POSITION", "分拣卸车岗"),
    SEND_CAR_POSITION("SEND_CAR_POSITION", "分拣发货封车岗"),
    EXCEPTION_POSITION("EXCEPTION_POSITION", "拣运异常岗"),
    TYS_UNLOAD_CAR_POSITION("TYS_UNLOAD_CAR_POSITION", "转运卸车岗"),
    WEIGHT_VOLUME_CALIBRATE_POSITION("WEIGHT_VOLUME_CALIBRATE_POSITION", "称重量方校准岗"),
    ;

    private static final Map<String, String> FUNC_CODE_ENUM_MAP;

    static {
        FUNC_CODE_ENUM_MAP = new HashMap<String, String >();
        for (JyFuncCodeEnum jyFuncCodeEnum : JyFuncCodeEnum.values()) {
            FUNC_CODE_ENUM_MAP.put(jyFuncCodeEnum.code,jyFuncCodeEnum.getName());
        }
    }

    private String code;
    private String name;
    JyFuncCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static String getFuncNameByCode(String code) {
        return FUNC_CODE_ENUM_MAP.get(code);
    }
    
    public String getName() {
        return name;
    }
    
    public String getCode() {
        return code;
    }

}
