package com.jd.bluedragon.distribution.collection.entity;

import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import lombok.Data;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectionSumEntity
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/3/6 21:37
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
public class CollectionSumEntity {

    /**
     * 待集齐集合ID
     */
    private String collectionCode;

    /**
     * 待集齐集合统计查询维度
     * 比如按运单查询
     */
    private CollectionAggCodeTypeEnum aggCodeTypeEnum;

    /**
     * 统计各个维度的总数
     * 比如运单总数
     */
    private Integer aggSum;

    /**
     * 当前纬度下，各个统计单号的集齐数量
     * 比如，一个运单集齐多少件
     */
    private Integer aggCollectedNum;

    /**
     * 当前纬度下，各个统计单号的未集数量
     * 比如，一个运单未集多少件
     */
    private Integer aggNoneCollectedNum;

    /**
     * 当前纬度下，各个统计单号的多集数量
     * 比如，一个运单多集多少件
     */
    private Integer aggExtraCollectedNum;
}
