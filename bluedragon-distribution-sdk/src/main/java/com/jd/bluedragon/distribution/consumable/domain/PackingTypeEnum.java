package com.jd.bluedragon.distribution.consumable.domain;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hanjiaxing1 on 2018/9/10.
 */
public enum PackingTypeEnum {

    TY001("TY001", "纸箱,个"),
    TY002("TY002", "编织袋,个"),
    TY003("TY003", "木架,方"),
    TY004("TY004", "木箱,方"),
    TY005("TY005", "缓冲物（缠绕膜）,方"),
    TY006("TY006", "缓冲物（气泡膜）,米"),
    TY007("TY007", "缓冲物（珍珠棉）,张"),
    TY008("TY008", "木托盘,块"),
    TY009("TY009", "其他"),
    TY010("TY010", "分拣物资");

    private String typeCode;

    private String typeName;

    PackingTypeEnum(String typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public static Map<String, String> getEnumMap(){
        TreeMap<String, String> map = new TreeMap<String, String>();
        for (PackingTypeEnum packingTypeEnum : PackingTypeEnum.values()) {
            map.put(packingTypeEnum.getTypeCode(),packingTypeEnum.getTypeName());
        }
        return map;
    }
}
