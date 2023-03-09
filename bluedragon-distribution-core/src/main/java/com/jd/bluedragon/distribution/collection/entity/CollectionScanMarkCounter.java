package com.jd.bluedragon.distribution.collection.entity;

import lombok.Data;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectionScanMarkCounter
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/3/7 18:57
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
public class CollectionScanMarkCounter {

    /**
     * 聚合统计号
     */
    private String aggCode;

    /**
     * 聚合统计类型
     */
    private String aggCodeType;

    /**
     * 集合ID
     */
    private String collectionCode;

    /**
     * mark
     */
    private String collectedMark;

    /**
     * 数量
     */
    private Integer num;
}
