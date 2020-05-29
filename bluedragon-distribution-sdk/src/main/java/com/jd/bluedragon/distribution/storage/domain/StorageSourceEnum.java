package com.jd.bluedragon.distribution.storage.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 暂存来源枚举
 * <p>
 *     1、金鹏暂存
 *     2、快运暂存
 * </p>
 *
 * @author: hujiping
 * @date: 2020/5/6 20:49
 */
public enum StorageSourceEnum {

    JP_STORAGE(1,"金鹏暂存"),
    KY_STORAGE(2,"快运暂存");
    private Integer code;
    private String name;
    private static Map<Integer, StorageSourceEnum> codeMap;
    public static Map<Integer, String> ArBookingSpaceTransportTypeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, StorageSourceEnum>();
        ArBookingSpaceTransportTypeMap = new HashMap<Integer, String>();
        for (StorageSourceEnum _enum : StorageSourceEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
            ArBookingSpaceTransportTypeMap.put(_enum.getCode(), _enum.getName());
        }
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static StorageSourceEnum getEnumByKey(Integer code) {
        return codeMap.get(code);
    }

    /**
     * 通过编码获取规则类型名称
     *
     * @param code 编码
     * @return 规则类型
     */
    public static String getNameByKey(Integer code) {
        StorageSourceEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知规则类型";
    }

    StorageSourceEnum(Integer code, String name) {
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
