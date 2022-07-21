package com.jd.bluedragon.distribution.jy.enums;

public enum GoodsTypeEnum {


    // 生鲜
    FRESH("生鲜"),

    KA("KA"),

    // 医药
    MEDICINE("医药"),

    // 特快送
    FAST("特快送"),

    // 特快重货
    FAST_HEAVY("特快重货"),

    // 其他
    NONE("其他");
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    GoodsTypeEnum(String name) {
        this.name = name;
    }

    public static String getGoodsDesc(String goodsName){
        for(GoodsTypeEnum goodsTypeEnum : GoodsTypeEnum.values()){
            if(goodsTypeEnum.name().equals(goodsName)){
                return goodsTypeEnum.getName();
            }
        }
        return null;
    }
}
