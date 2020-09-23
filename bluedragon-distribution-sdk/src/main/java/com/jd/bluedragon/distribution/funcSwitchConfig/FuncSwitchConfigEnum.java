package com.jd.bluedragon.distribution.funcSwitchConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能开关配置枚举
 *
 * @author: hujiping
 * @date: 2020/9/16 16:46
 */
public enum FuncSwitchConfigEnum {

    // 核心业务功能
    FUNCTION_INSPECTION(1001,"DMS-WEB-FUNCTION-SWITCH-INSPECTION","验货"),
    FUNCTION_SORTING(2001,"DMS-WEB-FUNCTION-SWITCH-SORTING","分拣"),
    FUNCTION_SEND(3001,"DMS-WEB-FUNCTION-SWITCH-SEND","发货"),
    FUNCTION_SEAL_CAR(4001,"DMS-WEB-FUNCTION-SWITCH-SEAL-CAR","封车"),
    // 普通业务功能
    FUNCTION_SPOT_CHECK(9001,"DMS-WEB-FUNCTION-SWITCH-SPOT-CHECK","抽检"),
    FUNCTION_COLLECTION_ADDRESS(9002,"DMS-WEB-FUNCTION-SWITCH-COLLECTION-ADDRESS","集包地"),
    FUNCTION_PRE_SELL(9003,"DMS-WEB-FUNCTION-SWITCH-PRE-SELL","预售");

    private int code;
    // 权限码
    private String authCode;
    private String name;

    public static Map<Integer, FuncSwitchConfigEnum> codeMap;
    public static Map<Integer, String> interceptMenuEnumMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, FuncSwitchConfigEnum>();
        interceptMenuEnumMap = new HashMap<Integer, String>();

        for (FuncSwitchConfigEnum _enum : FuncSwitchConfigEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
            interceptMenuEnumMap.put(_enum.getCode(),_enum.getName());
        }
    }

    /**
     * 通过编码获取枚举
     */
    public static FuncSwitchConfigEnum getEnumByKey(int code) {
        return codeMap.get(code);
    }

    FuncSwitchConfigEnum(int code, String authCode, String name) {
        this.code = code;
        this.authCode = authCode;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
