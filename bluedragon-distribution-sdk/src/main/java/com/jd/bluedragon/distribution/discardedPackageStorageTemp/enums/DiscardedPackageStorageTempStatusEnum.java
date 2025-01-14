package com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 弃件暂存状态枚举
 *
 * @author fanggang7
 * @time 2021-03-31 14:57:05 周三
 */
public enum DiscardedPackageStorageTempStatusEnum {

    /**
     * 暂存
     */
    TEMP_STORAGE(0, "暂存"),

    /**
     * 出库
     */
    OUT_STOCK(1, "出库"),

    /**
     * 已认领
     */
    CLAIMED(2, "已认领"),
    /**
     * 已认领
     */
    STORAGE(3, "报废"),

    /**
     * 未知
     */
    UNKNOW(null, "未知"),
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (DiscardedPackageStorageTempStatusEnum enumItem : DiscardedPackageStorageTempStatusEnum.values()) {
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

    DiscardedPackageStorageTempStatusEnum(Integer code, String name) {
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
