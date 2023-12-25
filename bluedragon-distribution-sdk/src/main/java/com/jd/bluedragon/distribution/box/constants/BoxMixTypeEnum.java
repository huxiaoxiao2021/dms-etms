package com.jd.bluedragon.distribution.box.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 混装类型枚举
 * @author fanggang7
 * @time 2023-10-25 10:20:44 周三
 */
public enum BoxMixTypeEnum {

    NOT_MIX(0, "不混装"),
    MIX(1, "混装"),
    ;

    private Integer code;

    private String name;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    BoxMixTypeEnum(Integer code, String name) {
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

    public static BoxMixTypeEnum getFromCode(String code) {
        for (BoxMixTypeEnum item : BoxMixTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static Map<Integer,String> getMap(){
        Map<Integer,String> result = new HashMap<Integer, String>();
        for(BoxMixTypeEnum boxTypeEnum : BoxMixTypeEnum.values()){
            result.put(boxTypeEnum.getCode(),boxTypeEnum.getName());
        }
        return result;
    }

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (BoxMixTypeEnum enumItem : BoxMixTypeEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
        }
    }

}
