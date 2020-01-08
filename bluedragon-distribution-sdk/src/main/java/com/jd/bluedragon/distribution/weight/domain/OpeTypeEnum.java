package com.jd.bluedragon.distribution.weight.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 称重量方操作维度枚举
 *
 * @author: hujiping
 * @date: 2020/1/7 17:05
 */
public enum OpeTypeEnum {

    PLATE_PRINT(1, "平台打印称重"),
    SITE_PLATE_PRINT(2, "站点平台打印称重"),
    FAST_TRANSPORT_PRINT(3, "快运称重"),
    PACKAGE_WEIGH_PRINT(4, "包裹称重"),
    BATCH_SORT_WEIGH_PRINT(5, "批量分拣称重"),
    FIELD_PRINT(6, "驻场打印称重"),
    SWITCH_BILL_PRINT(7, "换单打印称重");

    private Integer code;

    private String name;

    private static Map<Integer, OpeTypeEnum> codeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, OpeTypeEnum>();

        for (OpeTypeEnum _enum : OpeTypeEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }

    OpeTypeEnum(Integer code, String name) {
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

    public static Map<Integer, OpeTypeEnum> getCodeMap() {
        return codeMap;
    }

    public static void setCodeMap(Map<Integer, OpeTypeEnum> codeMap) {
        OpeTypeEnum.codeMap = codeMap;
    }
}
