package com.jd.bluedragon.distribution.box.constants;

import java.util.*;

/**
 * 新版箱号大类枚举
 * @author fanggang7
 * @time 2023-10-25 10:20:44 周三
 */
public enum BoxTypeV2Enum {

    TYPE_BC("BC", "同城", Collections.<String>emptyList()),
    TYPE_TC("TC", "退货组", Collections.<String>emptyList()),
    TYPE_WJ("WJ", "文件", Collections.<String>emptyList()),
    TYPE_TA("TA", "特安", Collections.<String>emptyList()),
    TYPE_LL("LL", "笼车/围板箱", Arrays.asList("BW","TC","WJ","TA","BC")),
    TYPE_BX("BX", "正向虚拟", Collections.<String>emptyList()),
    TYPE_WM("BW", "仓储", Collections.<String>emptyList());


    private String code;

    private String name;

    public static Map<String, String> ENUM_MAP;

    public static List<String> ENUM_LIST;

    /**
     * 支持内嵌的箱号类型
     */
    private List<String> supportEmbeddedTypes;

    BoxTypeV2Enum(String code, String name,List<String> supportEmbeddedTypes) {
        this.code = code;
        this.name = name;
        this.supportEmbeddedTypes =supportEmbeddedTypes;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<String> getSupportEmbeddedTypes() {
        return supportEmbeddedTypes;
    }

    public void setSupportEmbeddedTypes(List<String> supportEmbeddedTypes) {
        this.supportEmbeddedTypes = supportEmbeddedTypes;
    }

    public String toString() {
        return this.getCode() + "-" + this.getName();
    }

    public static BoxTypeV2Enum getFromCode(String code) {
        for (BoxTypeV2Enum item : BoxTypeV2Enum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static Map<String,String> getMap(){
        Map<String,String> result = new HashMap<String, String>();
        for(BoxTypeV2Enum boxTypeEnum : BoxTypeV2Enum.values()){
            result.put(boxTypeEnum.getCode(),boxTypeEnum.getName());
        }
        return result;
    }

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<String, String>();
        ENUM_LIST = new ArrayList<String>();
        for (BoxTypeV2Enum enumItem : BoxTypeV2Enum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
        }
    }

}
