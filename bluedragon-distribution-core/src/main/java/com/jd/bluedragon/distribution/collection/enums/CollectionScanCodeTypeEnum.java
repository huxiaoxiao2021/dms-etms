package com.jd.bluedragon.distribution.collection.enums;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.enums
 * @ClassName: CollectionScanCodeTypeEnum
 * @Description: 拣运集齐能力域-待集齐单号的单号类型
 * @Author： wuzuxiang
 * @CreateDate 2023/2/27 15:31
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public enum CollectionScanCodeTypeEnum {

    package_code("包裹号"),

    @Deprecated
    waybill_code("运单号"),

    @Deprecated
    box_code("箱号"),

    ;

    private final String mark;

    CollectionScanCodeTypeEnum(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
