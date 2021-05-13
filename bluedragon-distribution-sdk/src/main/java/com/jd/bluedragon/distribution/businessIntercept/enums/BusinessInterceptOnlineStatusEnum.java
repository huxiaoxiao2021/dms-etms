package com.jd.bluedragon.distribution.businessIntercept.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拦截报表用到的在线离线状态枚举
 *
 * @author fanggang7
 * @time 2020-12-11 10:50:24 周五
 */
public enum BusinessInterceptOnlineStatusEnum {

    /**
     * 在线
     */
    ONLINE(1, "在线"),
    /**
     * 离线
     */
    OFFLINE(2, "离线")
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (BusinessInterceptOnlineStatusEnum enumItem : BusinessInterceptOnlineStatusEnum.values()) {
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

    BusinessInterceptOnlineStatusEnum(Integer code, String name) {
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
