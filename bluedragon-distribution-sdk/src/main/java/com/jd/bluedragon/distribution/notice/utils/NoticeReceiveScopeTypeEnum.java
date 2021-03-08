package com.jd.bluedragon.distribution.notice.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拦截报表用到的操作维度枚举
 *
 * @author fanggang7
 * @time 2020-12-11 10:50:24 周五
 */
public enum NoticeReceiveScopeTypeEnum {

    /**
     * 全部
     */
    ALL(0, "全部"),
    /**
     * 仅分拣工作台
     */
    WORKBENCH(1, "仅分拣工作台"),
    /**
     * 仅安卓PDA
     */
    PDA_ANDROID(2, "仅安卓PDA"),
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (NoticeReceiveScopeTypeEnum enumItem : NoticeReceiveScopeTypeEnum.values()) {
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

    NoticeReceiveScopeTypeEnum(Integer code, String name) {
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
