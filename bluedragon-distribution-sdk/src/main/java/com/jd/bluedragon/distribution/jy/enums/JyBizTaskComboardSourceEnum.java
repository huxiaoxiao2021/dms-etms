package com.jd.bluedragon.distribution.jy.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liwenji
 * @date 2022-12-23 14:04
 */
public enum JyBizTaskComboardSourceEnum {

    ARTIFICIAL(1, "人工"),
    AUTOMATION(2, "自动化");

    private Integer code;
    private String name;

    private static final Map<Integer, JyBizTaskComboardSourceEnum> JY_BIZ_TASK_COMBOARD_SOURCE_ENUM_MAP;
    static {
        JY_BIZ_TASK_COMBOARD_SOURCE_ENUM_MAP = new HashMap<Integer, JyBizTaskComboardSourceEnum>();
        for (JyBizTaskComboardSourceEnum _enum : JyBizTaskComboardSourceEnum.values()) {
            JY_BIZ_TASK_COMBOARD_SOURCE_ENUM_MAP.put(_enum.getCode(), _enum);
        }
    }
    JyBizTaskComboardSourceEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        JyBizTaskComboardSourceEnum _enum = JY_BIZ_TASK_COMBOARD_SOURCE_ENUM_MAP.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
