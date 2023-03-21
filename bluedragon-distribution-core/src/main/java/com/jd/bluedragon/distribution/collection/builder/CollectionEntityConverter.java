package com.jd.bluedragon.distribution.collection.builder;

import com.jd.bluedragon.distribution.collection.entity.CollectionCodeEntity;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;

import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.converter
 * @ClassName: CollectionEntityConverter
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/3/21 16:01
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class CollectionEntityConverter {

    public static CollectionCodeEntity buildCollectionCodeEntity(Map<CollectionConditionKeyEnum, Object> elements,
        CollectionBusinessTypeEnum businessTypeEnum, String jqCode) {

        CollectionCodeEntity collectionCodeEntity = new CollectionCodeEntity(businessTypeEnum);
        collectionCodeEntity.addAllKey(elements);
        collectionCodeEntity.buildCollectionCondition();
        collectionCodeEntity.setCollectionCode(jqCode);
        return collectionCodeEntity;
    }


}
