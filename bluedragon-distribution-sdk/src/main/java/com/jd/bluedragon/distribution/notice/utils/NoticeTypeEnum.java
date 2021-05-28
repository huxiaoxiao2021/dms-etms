package com.jd.bluedragon.distribution.notice.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lixin39
 * @Description 通知类型枚举
 * @ClassName NoticeTypeEnum
 * @date 2019/4/17
 */
public enum NoticeTypeEnum {

    /**
     * 通知
     */
    NOTICE(1, "通知"),

    /**
     * 说明
     */
    DESCRIPTION(2, "说明"),

    /**
     * 警告
     */
    WARNING(3, "警告"),

    /**
     * 处罚
     */
    PUNISHMENT(4, "处罚"),

    /**
     * 文档
     */
    DOCUMENT(5, "文档"),

    /**
     * 文档
     */
    KNOWLEDGE(6, "知识库");

    private int code;

    private String name;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    NoticeTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据编码获取通知类型信息
     *
     * @param code
     * @return
     */
    public static NoticeTypeEnum getEnum(int code) {
        for (NoticeTypeEnum type : NoticeTypeEnum.values()){
            if (type.getCode() == code){
                return type;
            }
        }
        return null;
    }

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (NoticeTypeEnum enumItem : NoticeTypeEnum.values()) {
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
}
