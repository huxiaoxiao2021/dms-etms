package com.jd.bluedragon.distribution.collection.builder;

import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.builder
 * @ClassName: CollectionConditionBuilder
 * @Description: 构建 CollectionCondition的key-value对象，用于collectionCondition的生成
 * @Author： wuzuxiang
 * @CreateDate 2023/2/28 14:41
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class CollectionConditionBuilder {

    /**
     *
     */
    private CollectionBusinessTypeEnum collectionBusinessType;

    /**
     * 组成collectionCondition的key-value的持有map，用于组成特定CollectionBusinessTypeEnum下的condition
     */
    private final Map<CollectionConditionKeyEnum, Object> collectionConditionKeyMap;

    private String collectionCondition;

    /**
     * 默认构造器
     */
    public CollectionConditionBuilder() {
        this.collectionConditionKeyMap = new HashMap<>(CollectionConditionKeyEnum.values().length);
    }

    public CollectionConditionBuilder(Map<CollectionConditionKeyEnum, Object> collectionConditionKeyMap) {
        this.collectionConditionKeyMap = collectionConditionKeyMap;
    }


}
