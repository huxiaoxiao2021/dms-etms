package com.jd.bluedragon.distribution.jy.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作来源枚举
 *
 * @author fanggang7
 * @time 2023-04-18 15:42:29 周二
 */
public enum JyBizOpereateSourceEnum {

    MANUAL(1,"人工手动"),
    SYSTEM(2,"系统自动"),
    ;

    private Integer code;
    private String name;
    public static Map<Integer, String> ENUM_MAP;
    public static List<Integer> ENUM_LIST;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (JyBizOpereateSourceEnum _enum : JyBizOpereateSourceEnum.values()) {
            ENUM_MAP.put(_enum.getCode(), _enum.getName());
            ENUM_LIST.add(_enum.getCode());
        }

    }

    JyBizOpereateSourceEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 通过code获取name
     *
     * @param code 编码
     * @return string
     */
    public static String getEnumNameByCode(Integer code) {
        return ENUM_MAP.get(code);
    }

}
