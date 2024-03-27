package com.jd.bluedragon.common.dto.operation.workbench.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/19 11:21
 * @Description:
 */
public enum JyExpSaveTypeEnum {

    TEMP_SAVE(0,"暂存"),
    SAVE(1,"保存"),
    ;


    private final Integer code;

    private final String text;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    JyExpSaveTypeEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static JyExpSaveTypeEnum getEnumByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (JyExpSaveTypeEnum value : JyExpSaveTypeEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

    static {
        // 将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (JyExpSaveTypeEnum enumItem : JyExpSaveTypeEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getText());
            ENUM_LIST.add(enumItem.getCode());
        }
    }

}
