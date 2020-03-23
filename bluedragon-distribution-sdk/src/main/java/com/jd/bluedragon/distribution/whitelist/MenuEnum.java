package com.jd.bluedragon.distribution.whitelist;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lijie
 * @date 2020/3/18 22:39
 */
public enum MenuEnum {
    INSPECTION(1,"验货"),
    SEND(2,"发货"),
    SORTING(3,"分拣"),
    SEALCAR(4,"封车");

    private int code;
    private String name;

    private static Map<Integer, MenuEnum> codeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, MenuEnum>();

        for (MenuEnum _enum : MenuEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }

    /**
     * 通过编码获取枚举
     */
    public static MenuEnum getEnumByKey(int code) {
        return codeMap.get(code);
    }

    MenuEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
