package com.jd.bluedragon.distribution.collection.builder;

import com.jd.bluedragon.distribution.collection.entity.*;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionStatusEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.*;
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

    public static List<CollectionAggCodeCounter> convertCollectionCollectedMarkCounterToCollectionAggCodeCounter(
        List<CollectionCollectedMarkCounter> collectionCollectedMarkCounters, String collectedMark) {

        if (CollectionUtils.isEmpty(collectionCollectedMarkCounters)) {
            return Collections.emptyList();
        }

        Map<String, List<CollectionCollectedMarkCounter>> effectiveMarkCounterMap = collectionCollectedMarkCounters.parallelStream().collect(Collectors.groupingBy(
            collectionCollectedMarkCounter -> collectionCollectedMarkCounter.getCollectionCode()
                .concat(",")
                .concat(collectionCollectedMarkCounter.getAggCode())
                .concat(",")
                .concat(collectionCollectedMarkCounter.getAggCodeType())));

        List<CollectionAggCodeCounter> collectionAggCodeCounters = new Vector<>();
        effectiveMarkCounterMap.forEach((key, itemMarkCounters) -> {
            String[] itemKey = key.split(",");
            String collectionCode = itemKey[0];
            String aggCode1 = itemKey[1];
            String aggCodeType = itemKey[2];

            CollectionAggCodeCounter aggCodeCounter = CollectionAggCodeCounter.builder()
                .collectionCode(collectionCode)
                .aggCode(aggCode1)
                .aggCodeType(aggCodeType)
                .aggMark(
                    itemMarkCounters.parallelStream().map(CollectionCollectedMarkCounter::getAggMark).filter(StringUtils::isNotEmpty).findAny().orElse("")
                )
                .ts(itemMarkCounters.parallelStream().map(CollectionCollectedMarkCounter::getTs).filter(Objects::nonNull).max(Timestamp::compareTo).orElse(new Timestamp(0)))
                .sumScanNum(
                    itemMarkCounters.parallelStream()
                        .filter(
                            item -> !CollectionStatusEnum.extra_collected.getStatus().equals(item.getCollectedStatus())
                        )
                        .mapToInt(CollectionCollectedMarkCounter::getNumber)
                        .sum()
                )
                .collectedNum(
                    itemMarkCounters.parallelStream()
                        .filter(
                            item -> CollectionStatusEnum.collected.getStatus().equals(item.getCollectedStatus())
                        )
                        .mapToInt(CollectionCollectedMarkCounter::getNumber)
                        .sum()
                )
                .noneCollectedNum(
                    itemMarkCounters.parallelStream()
                        .filter(
                            item -> CollectionStatusEnum.none_collected.getStatus().equals(item.getCollectedStatus())
                        )
                        .mapToInt(CollectionCollectedMarkCounter::getNumber)
                        .sum()
                )
                .extraCollectedNum(
                    itemMarkCounters.parallelStream()
                        .filter(
                            item -> CollectionStatusEnum.extra_collected.getStatus().equals(item.getCollectedStatus())
                        )
                        .mapToInt(CollectionCollectedMarkCounter::getNumber)
                        .sum()
                )
                .noneMarkNoneCollectedNum(
                    itemMarkCounters.parallelStream()
                        .filter(
                            item -> CollectionStatusEnum.none_collected.getStatus().equals(item.getCollectedStatus()) && StringUtils
                                .isEmpty(item.getCollectedMark())
                        )
                        .mapToInt(CollectionCollectedMarkCounter::getNumber)
                        .sum()
                )
                .innerMarkCollectedNum(
                    itemMarkCounters.parallelStream()
                        .filter(
                            item -> CollectionStatusEnum.collected.getStatus().equals(item.getCollectedStatus())
                                && Objects.equals(item.getCollectedMark(), collectedMark)
                        )
                        .mapToInt(CollectionCollectedMarkCounter::getNumber)
                        .sum()
                )
                .innerMarkNoneCollectedNum(
                    itemMarkCounters.parallelStream()
                        .filter(
                            item -> CollectionStatusEnum.none_collected.getStatus().equals(item.getCollectedStatus())
                                && Objects.equals(item.getCollectedMark(), collectedMark)
                        )
                        .mapToInt(CollectionCollectedMarkCounter::getNumber)
                        .sum()
                )
                .innerMarkExtraCollectedNum(
                    itemMarkCounters.parallelStream().filter(
                        item -> CollectionStatusEnum.extra_collected.getStatus().equals(item.getCollectedStatus())
                            && Objects.equals(item.getCollectedMark(), collectedMark)
                    ).mapToInt(CollectionCollectedMarkCounter::getNumber).sum()
                )
                .outMarkCollectedNum(
                    itemMarkCounters.parallelStream()
                        .filter(
                            item -> CollectionStatusEnum.collected.getStatus().equals(item.getCollectedStatus())
                                && !Objects.equals(item.getCollectedMark(), collectedMark)
                        )
                        .mapToInt(CollectionCollectedMarkCounter::getNumber)
                        .sum()
                )
                .outMarkNoneCollectedNum(
                    itemMarkCounters.parallelStream()
                        .filter(
                            item -> CollectionStatusEnum.none_collected.getStatus().equals(item.getCollectedStatus())
                                && !Objects.equals(item.getCollectedMark(), collectedMark)
                        )
                        .mapToInt(CollectionCollectedMarkCounter::getNumber)
                        .sum()
                )
                .outMarkExtraCollectedNum(
                    itemMarkCounters.parallelStream().filter(
                        item -> CollectionStatusEnum.extra_collected.getStatus().equals(item.getCollectedStatus())
                            && !Objects.equals(item.getCollectedMark(), collectedMark)
                    ).mapToInt(CollectionCollectedMarkCounter::getNumber).sum()
                )
                .build();

            collectionAggCodeCounters.add(aggCodeCounter);


        });

        List<CollectionAggCodeCounter> result = new Vector<>();

        //过滤一些多扫冗余的数据
        collectionAggCodeCounters.parallelStream().collect(Collectors.groupingBy(
            collectionAggCodeCounter -> collectionAggCodeCounter.getAggCode().concat("-").concat(collectionAggCodeCounter.getAggCodeType()))
        ).forEach(
            (key, itemList) -> {
                if (itemList.size() == 1) {
                    result.addAll(itemList);
                } else {
                    result.add(
                        itemList.parallelStream()
                            .filter(item -> item.getCollectedNum() > 0)
                            .findAny()
                            .orElse(itemList.get(0))
                    );
                }
            });

        return result;
    }

    public static Map<String, List<CollectionRecordDetailPo>> getCollectedDetailPoFromCollectionCreatorByAggCode(
        CollectionCreatorEntity collectionCreatorEntity, CollectionAggCodeTypeEnum aggCodeTypeEnum) {

        return collectionCreatorEntity.getCollectionScanCodeEntities().parallelStream().map(
            collectionScanCodeEntity -> CollectionRecordDetailPo.builder()
                .collectionCode(collectionCreatorEntity.getCollectionCodeEntity().getCollectionCode())
                .scanCode(collectionScanCodeEntity.getScanCode())
                .scanCodeType(collectionScanCodeEntity.getScanCodeType().name())
                .aggCode(
                    collectionScanCodeEntity.getCollectionAggCodeMaps().getOrDefault(aggCodeTypeEnum, "null")
                )
                .aggCodeType(aggCodeTypeEnum.name())
                .collectedStatus(CollectionStatusEnum.collected.getStatus())
                .collectedMark(collectionScanCodeEntity.getCollectedMark())
                .build())
            .collect(Collectors.groupingBy(CollectionRecordDetailPo::getAggCode));

    }


}
