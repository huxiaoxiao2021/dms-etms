package com.jd.bluedragon.distribution.weight.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 称重量方来源枚举
 *
 * @author: hujiping
 * @date: 2020/1/8 13:37
 */
public enum WeightSourceFromEnum {

    PRINT_DESK(1, "打印客户端");

    private Integer code;

    private String name;

    private static Map<Integer, WeightSourceFromEnum> codeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, WeightSourceFromEnum>();

        for (WeightSourceFromEnum _enum : WeightSourceFromEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }

    WeightSourceFromEnum(Integer code, String name) {
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

    public static Map<Integer, WeightSourceFromEnum> getCodeMap() {
        return codeMap;
    }

    public static void setCodeMap(Map<Integer, WeightSourceFromEnum> codeMap) {
        WeightSourceFromEnum.codeMap = codeMap;
    }
}
