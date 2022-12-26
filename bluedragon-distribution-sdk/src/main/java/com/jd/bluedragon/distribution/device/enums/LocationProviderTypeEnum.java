package com.jd.bluedragon.distribution.device.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备定位类型枚举
 *
 * @author fanggang7
 * @time 2022-12-02 20:01:55 周四
 */
public enum LocationProviderTypeEnum {

    /**
     * 网络
     */
    NETWORK_PROVIDER("network", "网络"),

    /**
     * GPS
     */
    GPS_PROVIDER("gps", "GPS"),

    /**
     * 被动定位
     */
    PASSIVE_PROVIDER("passive", "被动定位"),

    /**
     * 组合定位
     */
    FUSED_PROVIDER("fused", "组合定位"),

    ;

    public static Map<String, String> ENUM_MAP;

    public static List<String> ENUM_LIST;

    private String code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<String, String>();
        ENUM_LIST = new ArrayList<String>();
        for (LocationProviderTypeEnum enumItem : LocationProviderTypeEnum.values()) {
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

    LocationProviderTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
