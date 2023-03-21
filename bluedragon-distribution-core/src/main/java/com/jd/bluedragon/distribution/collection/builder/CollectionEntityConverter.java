package com.jd.bluedragon.distribution.collection.builder;

import com.jd.bluedragon.distribution.collection.entity.CollectionCodeEntity;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static List<String> getCollectionCodesFromCollectionCodeEntity(List<CollectionCodeEntity> collectionCodeEntities) {
        if (CollectionUtils.isEmpty(collectionCodeEntities)) {
            return Collections.emptyList();
        }
        return collectionCodeEntities.parallelStream().map(CollectionCodeEntity::getCollectionCode).collect(
            Collectors.toList());


    }


}
