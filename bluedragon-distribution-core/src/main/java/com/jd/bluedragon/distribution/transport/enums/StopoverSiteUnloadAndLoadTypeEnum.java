package com.jd.bluedragon.distribution.transport.enums;

import java.util.*;

/**
 * 经停站点装卸类型
 *
 * @author fanggang7
 * @time 2023-08-18 11:33:18 周五
 */
public enum StopoverSiteUnloadAndLoadTypeEnum {

    /**
     * 解封车后自动关闭卸车任务
     */
    ONLY_UNLOAD_NO_LOAD(1, "只卸不装"),

    /**
     * 卸车开始后一直不再卸车扫描，也未操作卸车完成，自动关闭卸车任务
     */
    ONLY_LOAD_NO_UNLOAD(2, "只装不卸"),

    UNKNOWN(-1, "未知"),
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<>();
        ENUM_LIST = new ArrayList<>();
        for (StopoverSiteUnloadAndLoadTypeEnum enumItem : StopoverSiteUnloadAndLoadTypeEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
        }
    }

    public static StopoverSiteUnloadAndLoadTypeEnum queryEnumByCode(Integer code) {
        for (StopoverSiteUnloadAndLoadTypeEnum value : StopoverSiteUnloadAndLoadTypeEnum.values()) {
            if(Objects.equals(value.getCode(), code)){
                return value;
            }
        }
        return UNKNOWN;
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

    StopoverSiteUnloadAndLoadTypeEnum(Integer code, String name) {
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
