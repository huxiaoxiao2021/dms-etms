package com.jd.bluedragon.distribution.collection.entity;

import lombok.Data;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectionCounter
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/3/7 16:06
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
public class CollectionCounter {

    /**
     * 集合ID
     */
    private String collectionCode;

    /**
     * 需要集齐总数
     */
    private Integer sumScanNum;

    /**
     * 已经集齐总数
     */
    private Integer collectedNum;

    /**
     * 未集齐总数
     */
    private Integer noneCollectedNum;

    /**
     * 多集总数
     */
    private Integer extraCollectedNum;


}
