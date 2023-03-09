package com.jd.bluedragon.distribution.collection.entity;

import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;
import lombok.Data;
import org.apache.commons.collections.MapUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectionCollectorEntity
 * @Description: 待集齐集合的集齐对象
 * @Author： wuzuxiang
 * @CreateDate 2023/3/2 22:53
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
public class CollectionCollectorEntity {

    /**
     * 包含所有待集齐集合的条件的key-value对象
     */
    private Map<CollectionConditionKeyEnum, Object> collectElements;

    private CollectionScanCodeEntity collectionScanCodeEntity;

    /**
     * 根据collectionElement创建所有的businessType和businessType下的condition
     * @return 返回所有命中的业务
     */
    public List<CollectionCodeEntity> genCollectionCodeEntities() {
        if (MapUtils.isEmpty(collectElements)) {
            return Collections.emptyList();
        }
        return Arrays.stream(CollectionBusinessTypeEnum.values()).filter(
            collectionBusinessTypeEnum ->
                collectionBusinessTypeEnum.getCollectionConditionKeys().parallelStream().allMatch(
                    collectionConditionKeyEnum -> collectElements.containsKey(collectionConditionKeyEnum)
                )
        ).map(collectionBusinessTypeEnum -> {
            CollectionCodeEntity collectionCodeEntity = new CollectionCodeEntity(collectionBusinessTypeEnum);
            collectionCodeEntity.addAllKey(collectElements);
            collectionCodeEntity.buildCollectionCondition();
            return collectionCodeEntity;
        }).collect(Collectors.toList());
    }

}
