package com.jd.bluedragon.distribution.consumable.domain;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hanjiaxing1 on 2018/9/10.
 */
public enum PackingTypeEnum {

    @Deprecated
    TY001("TY001", "纸箱,个"),
    @Deprecated
    TY002("TY002", "编织袋,个"),
    @Deprecated
    TY003("TY003", "木架,方"),
    @Deprecated
    TY004("TY004", "木箱,方"),
    @Deprecated
    TY005("TY005", "缓冲物（缠绕膜）,方"),
    @Deprecated
    TY006("TY006", "缓冲物（气泡膜）,米"),
    @Deprecated
    TY007("TY007", "缓冲物（珍珠棉）,张"),
    @Deprecated
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

    /**
     * 终端包装耗材重塑项目：
     * 下线分拣的包装耗材维护功能，设计到表【dms_consumable_relation】【packing_consumable_info】
     * 但是由于分拣有个物资发货的功能使用了包装耗材的基础信息维护表【packing_consumable_info】，导致上述两张表并不能完全下线
     * 因此保留分拣的物资发货需要的维护功能。即【分拣】【其他】两个类型的枚举，其他枚举全部下线，放到终端去维护。
     * @return
     */
    public static Map<String, String> getMaterialTypeEnumMap() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put(TY009.getTypeCode(), TY009.getTypeName());
        map.put(TY010.getTypeCode(), TY010.getTypeName());
        return map;
    }

    /**
     * （旧耗材）
     * 判断该耗材编码是不是木质类型的包装耗材
     * @param typeCode 包装耗材类型
     * @return
     */
    public static boolean isWoodenConsumable(String typeCode) {
        return TY003.getTypeCode().equals(typeCode) || TY004.getTypeCode().equals(typeCode) || TY008.getTypeCode().equals(typeCode);
    }
}
