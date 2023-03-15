package com.jd.bluedragon.distribution.collection.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

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
@AllArgsConstructor
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
     * 聚合统计的标识字段
     */
    private String aggMark;

    /**
     * 需要集齐总数
     *  == 0 代表他没有被集齐初始化，
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
     * 未到
     */
    private Integer noneMarkNoneCollectedNum;

    /**
     * 本车已集齐,
     * 本车已扫
     */
    private Integer innerMarkCollectedNum;

    /**
     * 本车未集齐
     * 本车待扫
     */
    private Integer innerMarkNoneCollectedNum;

    /**
     * 本车多扫
     */
    private Integer innerMarkExtraCollectedNum;

    /**
     * 在库已集齐
     * 非本车已扫
     */
    private Integer outMarkCollectedNum;

    /**
     * 在库未集齐
     */
    private Integer outMarkNoneCollectedNum;

    /**
     * 在库多扫
     */
    private Integer outMarkExtraCollectedNum;

    /**
     * 数据时间，可以判断数据是否发生更新
     */
    private Timestamp ts;

}
