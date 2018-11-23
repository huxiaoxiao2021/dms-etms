package com.jd.bluedragon.distribution.box.domain;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hanjiaxing1 on 2018/10/19.
 *
 * 由于箱号状态的取值无从查证，该枚举类不具备任何指导意义，只用于更新箱号发货状态缓存
 *
 */
public enum BoxStatusEnum {

    INIT_STATUS(1, "初始状态"),
    CANCELED_STATUS(2, "取消发货状态"),
    SENT_STATUS(5, "已发货状态");

    private Integer code;
    private String name;

    BoxStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.getCode() + "-" + this.getName();
    }

    public static Map<Integer, String> getEnumMap() {
        TreeMap<Integer, String> map = new TreeMap<Integer, String>();
        for (BoxStatusEnum boxStatusEnum : BoxStatusEnum.values()) {
            map.put(boxStatusEnum.getCode(), boxStatusEnum.getName());
        }
        return map;
    }
}
