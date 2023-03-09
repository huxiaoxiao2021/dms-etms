package com.jd.bluedragon.distribution.collection.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectedAggCodeCount
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/3/6 17:06
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
@Builder
@NoArgsConstructor
public class CollectionAggCodeCounter {

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

    /**
     * 无collectedMark的情况
     */
    private Integer noneCollectedMarkNum;

    /**
     * 相同collectedMark数量
     */
    private Integer innerCollectedMarkNum;

    /**
     * 不同collectedMark数量
     */
    private Integer outCollectedMarkNum;

}
