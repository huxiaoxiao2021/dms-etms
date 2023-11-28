package com.jd.bluedragon.distribution.box.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 承运类型美爵
 * @author fanggang7
 * @time 2023-10-25 10:20:44 周三
 */
public enum BoxCarriageTypeEnum {

    AIR(1,  "航空"),
    HIGHWAY(2, "公路"),
    RAILWAY(3, "铁路"),
    ;

    private Integer code;

    private String name;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    BoxCarriageTypeEnum(Integer code, String name) {
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
        return this.getCode()  + "-" + this.getName();
    }

    public static BoxCarriageTypeEnum getFromCode(String code) {
        for (BoxCarriageTypeEnum item : BoxCarriageTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static Map<Integer,String> getMap(){
        Map<Integer,String> result = new HashMap<Integer, String>();
        for(BoxCarriageTypeEnum boxTypeEnum : BoxCarriageTypeEnum.values()){
            result.put(boxTypeEnum.getCode(),boxTypeEnum.getName());
        }
        return result;
    }

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (BoxCarriageTypeEnum enumItem : BoxCarriageTypeEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
        }
    }

}
