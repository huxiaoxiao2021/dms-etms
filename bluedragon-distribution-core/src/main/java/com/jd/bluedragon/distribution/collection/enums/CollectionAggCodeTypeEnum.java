package com.jd.bluedragon.distribution.collection.enums;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.enums
 * @ClassName: CollectionAggCodeTypeEnum
 * @Description: 拣运能力域-每个待集齐集合下的聚合指标。
 *  一般来讲他与CollectionBusinessType的关系是一对多的关系。
 *  一个businessType对应多个aggCodeType。但是一个businessType对应的是哪几个aggCodeType是固定的
 *
 *  使用方式：一般来说，某个集齐业务下，我们关心的是哪种统计维度，我们就将其与businessType进行绑定，目前更多的是关注waybill_code维度
 * @Author： wuzuxiang
 * @CreateDate 2023/2/27 15:16
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public enum CollectionAggCodeTypeEnum {

    waybill_code("运单号"),

    vehicle_number("车牌号"),

    box_code("箱号")

    ;

    private final String name;

    CollectionAggCodeTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
