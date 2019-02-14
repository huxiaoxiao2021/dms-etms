package com.jd.bluedragon.distribution.express.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *  快运到齐查询状态枚举
 *  Created by  2017年11月14日13:55:08
 */
public enum ExpressStatusTypeEnum {

    //枚举值
    HAS_INSPECTION("0", "已验货"),
    HAS_SORTING("1", "已分拣"),
    HAS_INSPECTION_OR_HAS_SORTING("1,0", "已验货已分拣"),
    HAS_SORTING_OR_HAS_INSPECTION("0,1", "已分拣已验货"),
    HAS_SEND("2", "已发货");

    private String code;
    private String name;
    private static Map<String, ExpressStatusTypeEnum> codeMap;
    public static Map<String, String> expressStatusMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<String, ExpressStatusTypeEnum>();
        expressStatusMap = new HashMap<String, String>();
        for (ExpressStatusTypeEnum expressStatusEnum : ExpressStatusTypeEnum.values()) {
            codeMap.put(expressStatusEnum.getCode(), expressStatusEnum);
            expressStatusMap.put(expressStatusEnum.getCode(), expressStatusEnum.getName());
        }
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 快运查询状态类型
     */
    public static ExpressStatusTypeEnum getexpressStatusEnumByKey(String code) {
        return codeMap.get(code);
    }

    /**
     * 通过编码获取快运查询状态类型名称
     *
     * @param code 编码
     * @return 快运查询状态类型
     */
    public static String getNameByKey(String code) {
        ExpressStatusTypeEnum expressStatusEnum = codeMap.get(code);
        if (expressStatusEnum != null) {
            return expressStatusEnum.getName();
        }
        return "未知快运查询状态类型";
    }

    ExpressStatusTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.getCode() + "-" + this.getName();
    }


}
