package com.jd.bluedragon.distribution.weight.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 称重量方操作维度枚举
 *
 * @author: hujiping
 * @date: 2020/1/3 17:05
 */
public enum WeightOpeTypeEnum {

    BOX(1, "箱"),
    WAYBILL(2, "运单"),
    PACKAGE(3, "包裹");

    private Integer code;

    private String name;

    private static Map<Integer, WeightOpeTypeEnum> codeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, WeightOpeTypeEnum>();

        for (WeightOpeTypeEnum _enum : WeightOpeTypeEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }

    WeightOpeTypeEnum(Integer code, String name) {
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

    public static Map<Integer, WeightOpeTypeEnum> getCodeMap() {
        return codeMap;
    }

    public static void setCodeMap(Map<Integer, WeightOpeTypeEnum> codeMap) {
        WeightOpeTypeEnum.codeMap = codeMap;
    }
}
