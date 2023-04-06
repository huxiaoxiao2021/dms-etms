package com.jd.bluedragon.distribution.collection.entity;

import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectionCodeEntity
 * @Description: 待集齐集合的业务信息
 * @Author： wuzuxiang
 * @CreateDate 2023/2/28 14:50
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class CollectionCodeEntity {

    /**
     * 待集齐集合的集合ID
     */
    private String collectionCode;

    /**
     * 待集齐集合的业务类型
     */
    private final CollectionBusinessTypeEnum businessType;

    /**
     * 该待集齐业务的集合条件
     */
    private String collectionCondition;

    /**
     * 包含所有待集齐集合的条件的key-value对象
     */
    private final Map<CollectionConditionKeyEnum, Object> collectElements;

    /**
     * 不建议采用无参构造器，因为collectionCondition的组合需要有businessType去约束
     * 初始化构造器，必须要有businessType字段
     * @param businessType 待集齐集合的业务类型
     */
    public CollectionCodeEntity(CollectionBusinessTypeEnum businessType) {
        this.businessType = businessType;
        this.collectElements = new HashMap<>();
    }

    public void setCollectionCode(String collectionCode) {
        this.collectionCode = collectionCode;
    }

    /**
     * 按需组装CollectionConditionKeyMap
     * @param key CollectionConditionKeyEnum
     * @param value 对应的值
     * @return 当前对象
     */
    public CollectionCodeEntity addKey(CollectionConditionKeyEnum key, Object value) {
        this.collectElements.put(key, value);
        return this;
    }

    public CollectionCodeEntity addAllKey(Map<CollectionConditionKeyEnum, Object> collectionConditionKeyMap) {
        this.collectElements.putAll(collectionConditionKeyMap);
        return this;
    }

    /**
     * 构建collectionCondition的字符串形式
     * 格式为：key1:value1,key2:value2,key3:value3
     * 如果value为空，则使用"null"字符串代替
     * @return 当前对象
     */
    public CollectionCodeEntity buildCollectionCondition() {
        this.collectionCondition = CollectionConditionKeyEnum.getCondition(this.collectElements, this.businessType);
        return this;
    }

    public String getCollectionCode() {
        return collectionCode;
    }

    public CollectionBusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public String getCollectionCondition() {
        return collectionCondition;
    }

    public Map<CollectionConditionKeyEnum, Object> getCollectElements() {
        return collectElements;
    }
}
