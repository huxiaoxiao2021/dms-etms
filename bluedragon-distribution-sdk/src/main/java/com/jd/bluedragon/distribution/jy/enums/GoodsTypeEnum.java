package com.jd.bluedragon.distribution.jy.enums;

public enum GoodsTypeEnum {


    // 生鲜
    FRESH("生鲜",2),

    KA("KA",5),

    // 医药
    MEDICINE("医药",6),

    // 特快送
    FAST("特快送",1),

    // 特快重货
    FAST_HEAVY("特快重货",7),

    LUXURY( "特保单",3),

    EASYFROZEN("易冻损",4),

    // 其他
    NONE("其他",9);


    private String name;

    /**
     * 排序
     */
    private Integer order;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    GoodsTypeEnum(String name, Integer order) {
        this.name = name;
        this.order = order;
    }

    public static String getGoodsDesc(String goodsName){
        for(GoodsTypeEnum goodsTypeEnum : GoodsTypeEnum.values()){
            if(goodsTypeEnum.name().equals(goodsName)){
                return goodsTypeEnum.getName();
            }
        }
        return null;
    }

    /**
     * 根据产品名称获取排序值
     * @param goodsName
     * @return
     */
    public static Integer getGoodsOrder(String goodsName){
        for(GoodsTypeEnum goodsTypeEnum : GoodsTypeEnum.values()){
            if(goodsTypeEnum.name().equals(goodsName)){
                return goodsTypeEnum.getOrder();
            }
        }
        return null;
    }
}
