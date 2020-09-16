package com.jd.bluedragon.distribution.mixedPackageConfig.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 站点类型枚举
 * Created by zhangleqi on 2017/8/25
 */
public enum SiteTypeEnum {

    //枚举值
    SORTING_CENTER(64, "分拣中心"),
    SITE(4, "站点");

    private Integer code;
    private String name;
    private static Map<Integer, SiteTypeEnum> codeMap;
    public static Map<Integer, String> siteTypeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, SiteTypeEnum>();
        siteTypeMap = new HashMap<Integer, String>();
        for (SiteTypeEnum siteTypeEnum : SiteTypeEnum.values()) {
            codeMap.put(siteTypeEnum.getCode(), siteTypeEnum);
            siteTypeMap.put(siteTypeEnum.getCode(), siteTypeEnum.getName());
        }
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 站点枚举
     */
    public static SiteTypeEnum getsiteTypeEnumByKey(Integer code) {
        return codeMap.get(code);
    }

    /**
     * 通过编码获取站点名称
     *
     * @param code 编码
     * @return 站点名称
     */
    public static String getNameByKey(Integer code) {
        SiteTypeEnum siteTypeEnum = codeMap.get(code);
        if (siteTypeEnum != null) {
            return siteTypeEnum.getName();
        }
        return "未知站点";
    }

    SiteTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

//    public boolean equals(Integer code) {
//        return this.getCode().equals(code);
//    }


    public Integer getCode() {
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
