package com.jd.bluedragon.distribution.collection.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.enums
 * @ClassName: CollectionBusinessTypeEnum
 * @Description: 拣运集齐能力域-待集齐业务场景枚举。
 * 该枚举决定了待集齐集合的初始化方式，据定了消除条件collectionCondition的组合方式，同时也决定了这个集合下的CollectionAggCode的组合方式
 * 如果需要新增集齐业务类型，那么就需要新增枚举
 * @Author： wuzuxiang
 * @CreateDate 2023/2/27 15:05
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public enum CollectionBusinessTypeEnum {

    /**
     * 基于装运卸车扫描业务下的卸车集齐业务
     * 中转集齐
     */
    unload_collection("卸车集齐业务",
        Collections.singletonList(CollectionAggCodeTypeEnum.waybill_code),
        Arrays.asList(CollectionConditionKeyEnum.site_code, CollectionConditionKeyEnum.seal_car_code)
    ),

    /**
     * 末端集齐
     */
    all_site_collection("场地集齐业务",
        Collections.singletonList(CollectionAggCodeTypeEnum.waybill_code),
        Collections.singletonList(CollectionConditionKeyEnum.site_code)
    )

    ;

    /**
     * 待集齐业务的业务类型名称
     */
    private final String mark;

    /**
     * 待集齐业务的聚合统计方式
     */
    private final List<CollectionAggCodeTypeEnum> collectionAggCodeTypes;

    /**
     * 待集齐业务的条件组合方式
     * 不允许出现两种业务拥有一致的组合方式，我们认为如果这两个拥有一致的condition的组合那我们会觉得他们属于同一种业务类型
     */
    private final List<CollectionConditionKeyEnum> collectionConditionKeys;

    CollectionBusinessTypeEnum(String mark, List<CollectionAggCodeTypeEnum> collectionAggCodeTypes,
        List<CollectionConditionKeyEnum> collectionConditionKeys) {
        this.mark = mark;
        this.collectionAggCodeTypes = collectionAggCodeTypes;
        this.collectionConditionKeys = collectionConditionKeys;

        /*
         * todo 添加CollectionConditionKeys的约束
         */

    }

    public String getMark() {
        return mark;
    }

    public List<CollectionAggCodeTypeEnum> getCollectionAggCodeTypes() {
        return collectionAggCodeTypes;
    }

    public List<CollectionConditionKeyEnum> getCollectionConditionKeys() {
        return collectionConditionKeys;
    }

}
