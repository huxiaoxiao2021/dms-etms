package com.jd.bluedragon.distribution.collection.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.redis.service.impl.RedisCommonUtil;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.collection.dao.CollectionRecordDao;
import com.jd.bluedragon.distribution.collection.entity.*;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionStatusEnum;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.service
 * @ClassName: CollectionRecordServiceImpl
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/3/1 17:08
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service
@Slf4j
public class CollectionRecordServiceImpl implements CollectionRecordService{

    @Autowired
    private CollectionRecordDao collectionRecordDao;

    @Autowired
    private RedisCommonUtil redisCommonUtil;

    @Autowired
    private KvIndexDao kvIndexDao;

    @Override
    public String getJQCodeByBusinessType(CollectionCodeEntity collectionCodeEntity) {
        return null;
    }

    @Override
    public boolean initFullCollection(CollectionCreatorEntity collectionCreatorEntity, Result<Boolean> result) {
        /* 1. 必要的参数检查 */
        if (Objects.isNull(collectionCreatorEntity)) {
            return false;
        }
        if (!collectionCreatorEntity.checkEntity(result)) {
            log.error("初始化待集齐集合失败，退出初始化，失败原因：{}，业务参数信息：{}", result.getMessage(),
                JSON.toJSONString(collectionCreatorEntity.getCollectionCodeEntity()));
            return false;
        }

        String collectionCode = collectionCreatorEntity.getCollectionCodeEntity().getCollectionCode();
        /* 2. 检查当前的collectionCode下是否已经进行过初始化。如果存在，则初始化退出；如果没有，则继续初始化 */
        if (collectionRecordDao.countCollectionRecordByCollectionCode(collectionCode) > 0
            || collectionRecordDao.countCollectionRecordDetailByCollectionCode(collectionCode) > 0) {
            result.toFail("已经存在部分初始化数据，无法进行全量初始化");
            result.setData(false);
            log.error("初始化待集齐集合失败，退出初始化，失败原因：{}，业务参数信息：{}", result.getMessage(),
                JSON.toJSONString(collectionCreatorEntity.getCollectionCodeEntity()));
            return false;
        }

        /* 3. 构建明细表的数据，将所有的scanCode与aggCode进行展开生成全部detail数据 */
        /* 3.1 按照aggCodeType分批处理生成明细表和主表统计数据 */
        List<CollectionRecordDetailPo> collectionRecordDetailTotalPos = new Vector<>();
        List<CollectionRecordPo> collectionRecordTotalPos = new Vector<>();
        collectionCreatorEntity.getCollectionCodeEntity().getBusinessType().getCollectionAggCodeTypes().forEach(
            aggCodeTypeEnum -> {
                /* 生成明细表的数，并按照aggCode进行分组 */
                List<CollectionRecordDetailPo> itemDetails = collectionCreatorEntity.getCollectionScanCodeEntities().parallelStream().map(
                    collectionScanCodeEntity -> CollectionRecordDetailPo.builder()
                        .collectionCode(collectionCode)
                        .scanCode(collectionScanCodeEntity.getScanCode())
                        .scanCodeType(collectionScanCodeEntity.getScanCodeType().name())
                        .aggCode(collectionScanCodeEntity.getCollectionAggCodeMaps().getOrDefault(aggCodeTypeEnum, "null"))
                        .aggCodeType(aggCodeTypeEnum.name())
                        .collectedStatus(CollectionStatusEnum.none_collected.getStatus())
                        .build()).collect(Collectors.toList());
                collectionRecordDetailTotalPos.addAll(itemDetails);

                Map<String, List<CollectionRecordDetailPo>> collectionRecordDetailPoMaps =
                    itemDetails.parallelStream().collect(Collectors.groupingBy(CollectionRecordDetailPo::getAggCode));

                collectionRecordDetailPoMaps.forEach((aggCode, item) -> {
                    CollectionRecordPo collectionRecordPo = new CollectionRecordPo();
                    collectionRecordPo.setCollectionCode(collectionCode);
                    collectionRecordPo.setAggCode(aggCode);
                    collectionRecordPo.setAggCodeType(aggCodeTypeEnum.name());
                    collectionRecordPo.setIsCollected(Constants.NUMBER_ZERO);
                    collectionRecordPo.setIsExtraCollected(Constants.NUMBER_ZERO);
                    collectionRecordPo.setAggMark(null);//todo aggMark 初始化的时候设置值
                    collectionRecordPo.setSum(item.size());
                    collectionRecordTotalPos.add(collectionRecordPo);
                });
            });
        /* 保存明细表数据 */
        Lists.partition(collectionRecordDetailTotalPos, Constants.DEFAULT_PAGE_SIZE).forEach(
            collectionRecordDetailPos -> collectionRecordDao.batchInsertCollectionRecordDetail(collectionRecordDetailPos)
        );
        /* 保存统计表数据 */
        Lists.partition(collectionRecordTotalPos, Constants.DEFAULT_PAGE_SIZE).forEach(
            collectionRecordPos -> collectionRecordDao.batchInsertCollectionRecord(collectionRecordPos)
        );
        return true;
    }

    @Override
    public boolean initPartCollection(CollectionCreatorEntity collectionCreatorEntity, Result<Boolean> result) {
        /* 1. 必要的参数检查 */
        if (Objects.isNull(collectionCreatorEntity)) {
            return false;
        }
        if (!collectionCreatorEntity.checkEntity(result)) {
            log.error("初始化待集齐集合失败，退出初始化，失败原因：{}，业务参数信息：{}", result.getMessage(),
                JSON.toJSONString(collectionCreatorEntity.getCollectionCodeEntity()));
            return false;
        }

        String collectionCode = collectionCreatorEntity.getCollectionCodeEntity().getCollectionCode();
        /* 2. 构建明细表的数据，将所有的scanCode与aggCode进行展开生成全部detail数据 */
        /* 2.1 按照aggCodeType分批处理生成明细表和主表统计数据 */
        collectionCreatorEntity.getCollectionCodeEntity().getBusinessType().getCollectionAggCodeTypes().forEach(
            aggCodeTypeEnum -> {
                /* 2.2 生成明细表的数，并按照aggCode进行分组 */
                Map<String, List<CollectionRecordDetailPo>> collectionRecordDetailPoMaps =
                    collectionCreatorEntity.getCollectionScanCodeEntities().parallelStream().map(
                        collectionScanCodeEntity -> CollectionRecordDetailPo.builder()
                            .collectionCode(collectionCode)
                            .scanCode(collectionScanCodeEntity.getScanCode())
                            .scanCodeType(collectionScanCodeEntity.getScanCodeType().name())
                            .aggCode(
                                collectionScanCodeEntity.getCollectionAggCodeMaps().getOrDefault(aggCodeTypeEnum, "null")
                            )
                            .aggCodeType(aggCodeTypeEnum.name())
                            .collectedStatus(CollectionStatusEnum.none_collected.getStatus())
                            .build())
                        .collect(Collectors.groupingBy(CollectionRecordDetailPo::getAggCode));

                /*
                    2.3
                    在增量模式下，每个aggCodeType下有且只能包含一个aggCode，也就是collectionRecordDetailPoMaps的size只能是1
                    如果不是的话，会影响操作效率
                */
                if (collectionRecordDetailPoMaps.size() > 1) {
                    log.warn("进行增量初始化待集齐集合时，检测到该聚合类型{}下有多个聚合值，请根据性能检查是否需要再次拆分。集合ID为{}",
                        aggCodeTypeEnum.name(), collectionCode);
                }
                collectionRecordDetailPoMaps.forEach((aggCode, collectionRecordDetailPos) -> {

                    /* 2.4 检查当前数据下的明细表的数据是否已经存在，在增量模式下，不会对已有的数据造成影响 */
                    CollectionRecordDetailPo collectionRecordDetailCondition = CollectionRecordDetailPo.builder()
                        .collectionCode(collectionCode)
                        .aggCode(aggCode)
                        .aggCodeType(aggCodeTypeEnum.name())
                        .build();

                    List<CollectionRecordDetailPo> collectionRecordDetailPosExist =
                        collectionRecordDao.findCollectionRecordDetail(collectionRecordDetailCondition);

                    Map<String, List<CollectionRecordDetailPo>> collectionRecordDetailPosExistMap =
                        collectionRecordDetailPosExist.parallelStream().collect(Collectors.groupingBy(CollectionRecordDetailPo::getScanCode));

                    /* 如果待初始化的信息，已经在待集齐集合中存在，那么需要根据已经存在的信息选择更新状态或者是skip，分两拨处理 */
                    /* 首选过滤状态已经存在的情况，且存在的状态是未扫 */
                    List<CollectionRecordDetailPo> collectionRecordDetailPosNotExist = collectionRecordDetailPos.parallelStream().filter(
                            /* 首选过滤出不存在的情况 */
                            collectionRecordDetailPo -> !collectionRecordDetailPosExistMap.containsKey(collectionRecordDetailPo.getScanCode())
                    ).collect(Collectors.toList());

                    /* 新增到数据库中 */
                    Lists.partition(collectionRecordDetailPosNotExist, Constants.DEFAULT_PAGE_SIZE).forEach(
                        collectionRecordDetailPosNotExistItemList ->
                            collectionRecordDao.batchInsertCollectionRecordDetail(collectionRecordDetailPosNotExistItemList)
                    );

                    /* 在过滤出已经存在的情况下，且状态是多扫的状态下 */
                    List<CollectionRecordDetailPo> collectionRecordDetailPosExtraExist = collectionRecordDetailPos.parallelStream().filter(
                        /* 首选过滤出存在且标注多扫的状态 */
                        collectionRecordDetailPo ->
                            collectionRecordDetailPosExistMap.containsKey(collectionRecordDetailPo.getScanCode()) &&
                                collectionRecordDetailPosExistMap.get(collectionRecordDetailPo.getScanCode()).parallelStream().anyMatch(
                                    collectionRecordDetailPo1 -> CollectionStatusEnum.extra_collected.getStatus().equals(
                                        collectionRecordDetailPo1.getCollectedStatus())
                                )
                    ).peek(
                        /* 将状态改为多扫的状态置为已集齐的状态 */
                        collectionRecordDetailPo ->
                            collectionRecordDetailPo.setCollectedStatus(CollectionStatusEnum.collected.getStatus())
                    ).collect(Collectors.toList());

                    /* 更新到数据库中 */
                    collectionRecordDetailPosExtraExist.forEach(
                        collectionRecordDetailPo -> collectionRecordDao.updateCollectionRecordDetail(collectionRecordDetailPo)
                    );

                    /* 更新主表aggCode维度的统计信息 */
                    Integer sum = collectionRecordDetailPosExist.size() + collectionRecordDetailPosNotExist.size();

                    /* 计算是否集齐 */
                    Integer isCollected = collectionRecordDetailPosExist.parallelStream().anyMatch(
                        collectionRecordDetailPo ->
                            CollectionStatusEnum.none_collected.getStatus().equals(collectionRecordDetailPo.getCollectedStatus())
                    ) || collectionRecordDetailPosNotExist.size() > 0? Constants.NUMBER_ZERO : Constants.NUMBER_ONE;

                    /* 计算是否多集 */
                    Integer isExtraCollected = collectionRecordDetailPosNotExist.parallelStream().filter(
                        collectionRecordDetailPo ->
                            CollectionStatusEnum.extra_collected.getStatus().equals(collectionRecordDetailPo.getCollectedStatus())
                    ).anyMatch(
                        collectionRecordDetailPo ->
                            collectionRecordDetailPos.parallelStream().noneMatch(
                                collectionRecordDetailPo1 ->
                                    Objects.equals(collectionRecordDetailPo1.getScanCode(), collectionRecordDetailPo.getScanCode())
                            )
                    )? Constants.NUMBER_ONE  : Constants.NUMBER_ZERO;

                    CollectionRecordPo collectionRecordPo = new CollectionRecordPo();
                    collectionRecordPo.setCollectionCode(collectionCode);
                    collectionRecordPo.setAggCode(aggCode);
                    collectionRecordPo.setAggCodeType(aggCodeTypeEnum.name());
                    collectionRecordPo.setIsCollected(isCollected);
                    collectionRecordPo.setIsExtraCollected(isExtraCollected);
                    collectionRecordPo.setAggMark(null);//todo aggMark 初始化的时候设置值
                    collectionRecordPo.setSum(sum);
                    collectionRecordDao.insertCollectionRecord(collectionRecordPo);

                });

            });

        return true;
    }

    @Override
    public boolean initAndCollectedPartCollection(CollectionCreatorEntity collectionCreatorEntity,
        Result<Boolean> result) {
        return false;
    }

    @Override
    public boolean collectTheScanCode(CollectionCollectorEntity collectionCollectorEntity, Result<Boolean> result) {
        if (null == collectionCollectorEntity
            || StringUtils.isEmpty(collectionCollectorEntity.getCollectionScanCodeEntity().getScanCode())) {
            result.toFail("收集的待集齐单号不存在");
            result.setData(false);
            return false;
        }
        List<CollectionCodeEntity> collectionCodeEntities = collectionCollectorEntity.genCollectionCodeEntities();
        if (CollectionUtils.isEmpty(collectionCodeEntities)) {
            result.toFail("未查询到当前的待集齐数据 ");
            result.setData(false);
            return false;
        }

        String scanCode = collectionCollectorEntity.getCollectionScanCodeEntity().getScanCode();
        Map<CollectionAggCodeTypeEnum, String> element = collectionCollectorEntity.getCollectionScanCodeEntity().getCollectionAggCodeMaps();
        /* 根据condition去business_code_attribute表中查询所有的集合ID,使用kv_index做查询 */
        collectionCollectorEntity.genCollectionCodeEntities().parallelStream()
            .filter(collectionCodeEntity -> StringUtils.isNotEmpty(collectionCodeEntity.getCollectionCondition()))
            .flatMap((Function<CollectionCodeEntity, Stream<CollectionCodeEntity>>)collectionCodeEntity -> {
                List<String> JQCodes = kvIndexDao.queryByKeyword(collectionCodeEntity.getCollectionCondition());
                return JQCodes.parallelStream().map(jqCode -> {
                    CollectionCodeEntity codeEntity = new CollectionCodeEntity(collectionCodeEntity.getBusinessType());
                    codeEntity.setCollectionCode(jqCode);
                    codeEntity.addAllKey(collectionCollectorEntity.getCollectElements());
                    codeEntity.buildCollectionCondition();
                    return codeEntity;
                });
            }).forEach(collectionCodeEntity -> {
                /* 检查该待集齐集合中是否有这单，检查是否是待集齐的状态 */
                List<CollectionRecordDetailPo> scanDetailPos = collectionRecordDao.findCollectionRecordDetail(CollectionRecordDetailPo.builder()
                                                                .collectionCode(collectionCodeEntity.getCollectionCode())
                                                                .scanCode(scanCode)
                                                                .build());
                /* 取第一条，如果是已经集齐或者是多集状态则不进行操作直接退出 */
                if (CollectionUtils.isNotEmpty(scanDetailPos)
                    && (
                        CollectionStatusEnum.collected.getStatus().equals(scanDetailPos.get(0).getCollectedStatus())
                            || CollectionStatusEnum.extra_collected.getStatus().equals(scanDetailPos.get(0).getCollectedStatus()))) {
                    log.info("该单号【{}】在该待集齐集合【{}】中已经是集齐状态，无需重复收集", scanCode, collectionCodeEntity.getCollectionCode());
                    return;
                }

                /* 如果是不存在的情况，那么需要创建为多集齐的状态 */
                if (CollectionUtils.isEmpty(scanDetailPos)) {
                    /* 根据businessType和element创建多个待插入的明细表数据 */
                    List<CollectionRecordDetailPo> collectionRecordDetailPos = collectionCodeEntity.getBusinessType().getCollectionAggCodeTypes().parallelStream()
                        .map(aggCodeTypeEnum -> CollectionRecordDetailPo.builder()
                            .collectionCode(collectionCodeEntity.getCollectionCode())
                            .scanCode(scanCode)
                            .scanCodeType(collectionCollectorEntity.getCollectionScanCodeEntity().getScanCodeType().name())
                            .aggCode(element.getOrDefault(aggCodeTypeEnum, "null"))
                            .aggCodeType(aggCodeTypeEnum.name())
                            .collectedStatus(CollectionStatusEnum.extra_collected.getStatus())
                            .createTime(new Date())
                            .build())
                        .collect(Collectors.toList());

                    collectionRecordDao.batchInsertCollectionRecordDetail(collectionRecordDetailPos);

                    collectionRecordDetailPos.forEach(collectionRecordDetailPo -> {
                        /* 检查主表，是否需要进行插入 */
                        CollectionRecordPo condition = new CollectionRecordPo();
                        condition.setCollectionCode(collectionCodeEntity.getCollectionCode());
                        condition.setAggCode(collectionRecordDetailPo.getAggCode());
                        condition.setAggCodeType(collectionRecordDetailPo.getAggCodeType());
                        List<CollectionRecordPo> collectionRecordPo = collectionRecordDao.findCollectionRecord(condition);
                        if (CollectionUtils.isEmpty(collectionRecordPo)) {
                            condition.setIsCollected(Constants.NUMBER_ZERO);
                            condition.setSum(Constants.NUMBER_ONE);
                            condition.setIsExtraCollected(Constants.NUMBER_ONE);
                            collectionRecordDao.insertCollectionRecord(condition);
                        } else {
                            condition.setIsExtraCollected(Constants.NUMBER_ONE);
                            collectionRecordDao.updateCollectionRecord(condition);
                        }
                    });
                }
                if (CollectionUtils.isNotEmpty(scanDetailPos) && CollectionStatusEnum.none_collected.getStatus().equals(scanDetailPos.get(0).getCollectedStatus())) {
                    /* 检查是否有根据appCodeType重置aggCode的行为 */
                    List<CollectionAggCodeTypeEnum> existAggCodeTypeEnums = collectionCodeEntity.getBusinessType().getCollectionAggCodeTypes()
                        .parallelStream().filter(element::containsKey).collect(Collectors.toList());
                    List<CollectionAggCodeTypeEnum> notExistAggCodeTypeEnums = collectionCodeEntity.getBusinessType().getCollectionAggCodeTypes()
                        .parallelStream().filter(aggCodeTypeEnum -> !element.containsKey(aggCodeTypeEnum)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(existAggCodeTypeEnums)) {
                        existAggCodeTypeEnums.forEach(aggCodeTypeEnum ->
                            collectionRecordDao.updateCollectionRecordDetail(CollectionRecordDetailPo.builder()
                                .collectionCode(collectionCodeEntity.getCollectionCode())
                                .scanCode(scanCode)
                                .aggCode(element.getOrDefault(aggCodeTypeEnum, "null"))//此处不会出现null值，因为之前已经进行过filter过滤
                                .aggCodeType(aggCodeTypeEnum.name())
                                .collectedStatus(CollectionStatusEnum.collected.getStatus())
                                .collectedMark("")//todo collectedMark
                                .collectedTime(new Date())
                                .build()));
                    }
                    if (CollectionUtils.isNotEmpty(notExistAggCodeTypeEnums)) {
                        notExistAggCodeTypeEnums.forEach(aggCodeTypeEnum -> {
                            collectionRecordDao.updateCollectionRecordDetail(CollectionRecordDetailPo.builder()
                                .collectionCode(collectionCodeEntity.getCollectionCode())
                                .scanCode(scanCode)
                                .aggCodeType(aggCodeTypeEnum.name())
                                .aggCode(element.getOrDefault(aggCodeTypeEnum, "null"))//此处不会出现null值，因为之前已经进行过filter过滤
                                .collectedStatus(CollectionStatusEnum.collected.getStatus())
                                .collectedMark("")//todo collectedMark
                                .collectedTime(new Date())
                                .build());
                        });
                    }
                    /* 更新主表 */
                    List<CollectionRecordDetailPo> aggCodeDetailPos = collectionRecordDao.findAggCodeByScanCode(
                        CollectionRecordDetailPo.builder()
                            .collectionCode(collectionCodeEntity.getCollectionCode())
                            .scanCode(scanCode)
                            .build()
                    );
                    if (CollectionUtils.isEmpty(aggCodeDetailPos)) {
                        log.error("数据查询严重不一致，需要检查逻辑，集齐单号为：{}", collectionCodeEntity.getCollectionCode());
                        return;
                    }
                    aggCodeDetailPos.forEach(aggCodeDetailPo -> {
                        /* 根据collectionCode， aggCodeType， aggCode， 查询是否集齐 todo 使用redis的hSet，缓存不存在则进行查库*/
                        CollectionAggCodeCounter aggCodeCounter = collectionRecordDao.countAggCollectedByAggCode(
                            CollectionRecordDetailPo.builder()
                                .collectionCode(collectionCodeEntity.getCollectionCode())
                                .aggCode(aggCodeDetailPo.getAggCode())
                                .aggCodeType(aggCodeDetailPo.getAggCodeType())
                                .build());
                        /* 如果是已经集齐的aggCode的话，需要更新主标的aggCode的信息 */
                        if (null == aggCodeCounter) {
                            log.error("数据查询严重不一致，需要检查逻辑，集齐单号为：{}，聚合号为：{}", collectionCodeEntity.getCollectionCode(), aggCodeDetailPo.getAggCode());
                            return;
                        }
                        if (Objects.equals(aggCodeCounter.getCollectedNum(), aggCodeCounter.getSumScanNum())) {
                            CollectionRecordPo recordCondition = new CollectionRecordPo();
                            recordCondition.setCollectionCode(collectionCodeEntity.getCollectionCode());
                            recordCondition.setAggCode(aggCodeDetailPo.getAggCode());
                            recordCondition.setAggCodeType(aggCodeDetailPo.getAggCodeType());
                            recordCondition.setIsCollected(Constants.NUMBER_ONE);
                            collectionRecordDao.updateCollectionRecord(recordCondition);
                        }

                    });
                }
            });

        return true;
    }

    @Override
    public CollectionAggCodeCounter countCollectionStatusByAggCodeAndCollectionCode(String collectionCode, String aggCode,
        CollectionAggCodeTypeEnum aggCodeTypeEnum) {
        if (StringUtils.isEmpty(collectionCode) || StringUtils.isEmpty(aggCode) || null == aggCodeTypeEnum) {
            log.error("统计待集齐集合的信息参数不全，{}-{}-{}", collectionCode, aggCode, aggCodeTypeEnum);
            return null;
        }

        return collectionRecordDao.countAggCollectedByAggCode(
                                                                CollectionRecordDetailPo.builder()
                                                                    .collectionCode(collectionCode)
                                                                    .aggCode(aggCode)
                                                                    .aggCodeType(aggCodeTypeEnum.name())
                                                                    .build()
                                                            );

    }

    @Override
    public CollectionAggCodeCounter countCollectionStatusByAggCodeAndCollectionCodeWithCollectedMark(String collectionCode, String aggCode,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark) {

        if (StringUtils.isEmpty(collectionCode) || StringUtils.isEmpty(aggCode) || null == aggCodeTypeEnum) {
            log.error("统计待集齐集合的信息参数不全，{}-{}-{}", collectionCode, aggCode, aggCodeTypeEnum);
            return null;
        }

        CollectionAggCodeCounter collectionAggCodeCounter = collectionRecordDao.countAggCollectedByAggCode(
            CollectionRecordDetailPo.builder()
                .collectionCode(collectionCode)
                .aggCode(aggCode)
                .aggCodeType(aggCodeTypeEnum.name())
                .build()
        );
        if (null == collectionAggCodeCounter) {
            log.error("根据aggCode统计待集齐信息查询失败，未查询到需要集齐的数据");
            return collectionAggCodeCounter;
        }

        List<CollectionScanMarkCounter> collectionScanMarkCounters = collectionRecordDao.countAggCollectedByAggCodeWithMark(
            CollectionRecordDetailPo.builder()
                .collectionCode(collectionCode)
                .aggCode(aggCode)
                .aggCodeType(aggCodeTypeEnum.name())
                .build()
        );
        /* 理论上来讲，上一次查询有数据，这次查询也应该有数据 */
        collectionAggCodeCounter.setNoneMarkNoneCollectedNum(
            (int)collectionScanMarkCounters.parallelStream().filter(
                collectionScanMarkCounter -> StringUtils.isEmpty(collectionScanMarkCounter.getCollectedMark())).count()
        );
        collectionAggCodeCounter.setInnerMarkCollectedNum(
            (int)collectionScanMarkCounters.parallelStream().filter(
                collectionScanMarkCounter -> Objects
                    .equals(collectedMark, collectionScanMarkCounter.getCollectedMark())).count()
        );
        collectionAggCodeCounter.setOutMarkCollectedNum(
            (int)collectionScanMarkCounters.parallelStream().filter(
                collectionScanMarkCounter -> StringUtils.isNotEmpty(collectionScanMarkCounter.getCollectedMark()) && !Objects
                    .equals(collectedMark, collectionScanMarkCounter.getCollectedMark())).count()
        );

        return collectionAggCodeCounter;

    }

    @Override
    public Integer countNoneCollectedAggCodeNumByCollectionCode(CollectionCodeEntity collectionCodeEntity) {
        return collectionRecordDao.countNoneCollectedAggCodeByCollectionCode(collectionCodeEntity.getCollectionCode());
    }

    @Override
    public void sumCollection(Map<CollectionConditionKeyEnum, Object> element) {
        if (MapUtils.isEmpty(element)) {
            return ;
        }

        List<CollectionCodeEntity> collectionCodeEntities = Arrays.stream(CollectionBusinessTypeEnum.values()).filter(
            collectionBusinessTypeEnum ->
                collectionBusinessTypeEnum.getCollectionConditionKeys().parallelStream().allMatch(
                    collectionConditionKeyEnum -> element.containsKey(collectionConditionKeyEnum)
                )
        ).map(collectionBusinessTypeEnum -> {
            CollectionCodeEntity collectionCodeEntity = new CollectionCodeEntity(collectionBusinessTypeEnum);
            collectionCodeEntity.addAllKey(element);
            collectionCodeEntity.buildCollectionCondition();
            return collectionCodeEntity;
        }).collect(Collectors.toList());

        collectionCodeEntities.parallelStream().forEach(new Consumer<CollectionCodeEntity>() {
            @Override
            public void accept(CollectionCodeEntity collectionCodeEntity) {

            }
        });

        return ;
    }

    @Override
    public void sumCollectionByCollectionCode(String collectionCode) {
        if (StringUtils.isEmpty(collectionCode)) {
            log.info("待查询的集合ID不存在，查询失败");
            return;
        }


    }
}
