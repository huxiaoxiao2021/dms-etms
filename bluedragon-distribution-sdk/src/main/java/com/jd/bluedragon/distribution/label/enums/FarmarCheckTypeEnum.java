package com.jd.bluedragon.distribution.label.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 砝码校验标准枚举
 *
 * @author hujiping
 * @date 2022/8/22 5:20 PM
 */
public enum FarmarCheckTypeEnum {

    FARMAR_CHECK_TYPE_WEIGHT(0, "重量标准"),
    FARMAR_CHECK_TYPE_SIZE(1, "尺寸标准")
    ;

    FarmarCheckTypeEnum(int code, String name){
        this.code = code;
        this.name = name;
    }

    public final static List<Integer> LIST = Lists.newArrayList();

    static {
        for (FarmarCheckTypeEnum value : FarmarCheckTypeEnum.values()) {
            LIST.add(value.getCode());
        }
    }

    private int code;
    private String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
