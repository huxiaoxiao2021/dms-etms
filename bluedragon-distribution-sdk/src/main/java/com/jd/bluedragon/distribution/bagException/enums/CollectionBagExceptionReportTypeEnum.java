package com.jd.bluedragon.distribution.bagException.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集包异常举报类型
 *
 * @author fanggang7
 * @time 2020-09-23 21:17:01 周三
 */
public enum CollectionBagExceptionReportTypeEnum {

    /**
     * 上游虚假集包
     */
    UPSTREAM_FAKE(1, "上游虚假集包"),

    /**
     * 上游未集包
     */
    UPSTREAM_NOT_DONE(2, "上游未集包"),

    /**
     * 无异常
     */
    NO_EXCEPTION(3, "无异常");

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (CollectionBagExceptionReportTypeEnum enumItem : CollectionBagExceptionReportTypeEnum.values()) {
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

    CollectionBagExceptionReportTypeEnum(Integer code, String name) {
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
