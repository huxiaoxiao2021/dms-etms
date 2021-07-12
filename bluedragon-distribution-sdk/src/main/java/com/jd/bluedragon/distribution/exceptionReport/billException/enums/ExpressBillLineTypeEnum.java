package com.jd.bluedragon.distribution.exceptionReport.billException.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 异常面单举报，运单所属条线类型
 *
 * @author fanggang7
 * @time 2020-12-11 10:50:24 周五
 */
public enum ExpressBillLineTypeEnum {

    /**
     * 仓库
     */
    WAREHOUSE(1, "仓库"),

    /**
     * 终端
     */
    STATION(2, "终端站点"),
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (ExpressBillLineTypeEnum enumItem : ExpressBillLineTypeEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
        }
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

    ExpressBillLineTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
