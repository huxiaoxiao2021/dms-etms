package com.jd.bluedragon.distribution.collection.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.redis.service.impl.RedisCommonUtil;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.busineCode.jqCode.JQCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.collection.dao.CollectionRecordDao;
import com.jd.bluedragon.distribution.collection.entity.*;
import com.jd.bluedragon.distribution.collection.enums.*;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.fastjson.JSON;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private JQCodeService jqCodeService;

    @Override
    public String getJQCodeByBusinessType(CollectionCodeEntity collectionCodeEntity, String userErp) {
        if (null == collectionCodeEntity || null == collectionCodeEntity.getBusinessType()) {
            log.error("获取待集齐集合ID失败，参数错误：{}", JSON.toJSONString(collectionCodeEntity));
            return "";
        }
        String condition = collectionCodeEntity.buildCollectionCondition().getCollectionCondition();
        if (StringUtils.isEmpty(condition)) {
            log.error("获取待集齐集合ID的要素失败，参数错误：{}", JSON.toJSONString(collectionCodeEntity));
            return "";
        }
        String JQCode = kvIndexDao.queryRecentOneByKeyword(condition);
        if (StringUtils.isNotEmpty(JQCode)) {
            log.info("获取到已经创建的待集齐集合ID[{}],参数为:{}", JQCode, JSON.toJSONString(collectionCodeEntity));
            return JQCode;
        }
        return jqCodeService.createJQCode(collectionCodeEntity.getCollectElements(), collectionCodeEntity.getBusinessType(),
            BusinessCodeFromSourceEnum.DMS_WORKER_SYS, null == userErp? "none" : userErp);

    }

    @Override
    public List<CollectionCodeEntity> queryAllCollectionCodesByElement(Map<CollectionConditionKeyEnum, Object> elements,
        CollectionBusinessTypeEnum businessType) {

        return Arrays.stream(CollectionBusinessTypeEnum.values())
            .filter(
                businessTypeEnum -> null == businessType || businessTypeEnum.equals(businessType)
            )
            .flatMap((Function<CollectionBusinessTypeEnum, Stream<CollectionCodeEntity>>)businessTypeEnum -> {
                CollectionCodeEntity collectionCodeEntity = new CollectionCodeEntity(businessTypeEnum);
                collectionCodeEntity.addAllKey(elements);
                collectionCodeEntity.buildCollectionCondition();
                List<String> jqCodes =
                    new ArrayList<>(kvIndexDao.queryByKeyword(collectionCodeEntity.getCollectionCondition()));
                //fixme 查询多个列表的时候，给错的处理，不要
                return jqCodes.stream().map(jqCode -> {
                    log.info("根据condition:{}命中了待集齐集合ID:{}",collectionCodeEntity.getCollectionCondition(), jqCode);
                    CollectionCodeEntity collectionCodeEntity1 = new CollectionCodeEntity(businessTypeEnum);
                    collectionCodeEntity1.addAllKey(elements);
                    collectionCodeEntity1.buildCollectionCondition();
                    collectionCodeEntity1.setCollectionCode(jqCode);
                    return collectionCodeEntity1;
                });
            }).collect(Collectors.toList());
    }

    @Override
    @JProfiler(jKey = "DMS.CORE.CollectionRecordService.initFullCollection", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {
        JProEnum.TP,JProEnum.FunctionError})
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
                    collectionRecordPo.setAggMark(collectionCreatorEntity.getCollectionAggMarks().getOrDefault(aggCode, ""));
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
    @JProfiler(jKey = "DMS.CORE.CollectionRecordService.initPartCollection", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {
        JProEnum.TP,JProEnum.FunctionError})
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
                            .collectedMark(collectionScanCodeEntity.getCollectedMark())
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
                    List<CollectionRecordDetailPo> collectionRecordDetailPosExist =
                        collectionRecordDao.findCollectionRecordDetail(CollectionRecordDetailPo.builder()
                            .collectionCode(collectionCode)
                            .aggCode(aggCode)
                            .aggCodeType(aggCodeTypeEnum.name())
                            .build());

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

                    /* 过滤状态已经存在的情况，但collectedMark不同的情况 */
                    List<CollectionRecordDetailPo> collectionRecordDetailPosExistWithOutMark = collectionRecordDetailPos.parallelStream().filter(
                        collectionRecordDetailPo ->
                            collectionRecordDetailPosExistMap.containsKey(collectionRecordDetailPo.getScanCode())
                                && collectionRecordDetailPosExistMap.get(collectionRecordDetailPo.getScanCode())
                                .parallelStream().anyMatch(item -> !Objects.equals(item.getCollectedMark(), collectionRecordDetailPo.getCollectedMark())
                                    && !CollectionStatusEnum.extra_collected.getStatus().equals(item.getCollectedStatus()))
                    ).collect(Collectors.toList());
                    /* 更新到数据库中 */
                    collectionRecordDetailPosExistWithOutMark.forEach(
                        collectionRecordDetailPo -> collectionRecordDao.updateCollectionRecordDetail(collectionRecordDetailPo)
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
                    collectionRecordPo.setAggMark(collectionCreatorEntity.getCollectionAggMarks().getOrDefault(aggCode,""));
                    collectionRecordPo.setSum(sum);
                    collectionRecordDao.insertCollectionRecord(collectionRecordPo);

                });

            });

        return true;
    }

    @Override
    public boolean initAndCollectedPartCollection(CollectionCreatorEntity collectionCreatorEntity,
        Result<Boolean> result) {
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
                            .collectedStatus(CollectionStatusEnum.collected.getStatus())
                            .collectedMark(collectionScanCodeEntity.getCollectedMark())
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
                    List<CollectionRecordDetailPo> collectionRecordDetailPosExist =
                        collectionRecordDao.findCollectionRecordDetail(CollectionRecordDetailPo.builder()
                            .collectionCode(collectionCode)
                            .aggCode(aggCode)
                            .aggCodeType(aggCodeTypeEnum.name())
                            .build());

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

                    /* 在过滤出已经存在的情况下，且状态是多扫或待扫的状态下 */
                    List<CollectionRecordDetailPo> collectionRecordDetailPosExtraExist = collectionRecordDetailPos.parallelStream().filter(
                        /* 首选过滤出存在且标注多扫或者待扫的状态 */
                        collectionRecordDetailPo ->
                            collectionRecordDetailPosExistMap.containsKey(collectionRecordDetailPo.getScanCode()) &&
                                collectionRecordDetailPosExistMap.get(collectionRecordDetailPo.getScanCode()).parallelStream().anyMatch(
                                    collectionRecordDetailPo1 -> CollectionStatusEnum.extra_collected.getStatus().equals(
                                        collectionRecordDetailPo1.getCollectedStatus()) || CollectionStatusEnum.none_collected.getStatus().equals(
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

                    List<CollectionScanMarkCounter> collectionScanMarkCounters = collectionRecordDao.sumAggCollectedByAggCode(CollectionRecordDetailPo.builder()
                        .collectionCode(collectionCode)
                        .aggCode(aggCode)
                        .aggCodeType(aggCodeTypeEnum.name())
                        .build());

                    //计算总数
                    Integer sum = collectionScanMarkCounters.parallelStream()
                        .filter(
                            collectionScanMarkCounter ->
                                CollectionStatusEnum.none_collected.getStatus().equals(collectionScanMarkCounter.getCollectedStatus())
                            || CollectionStatusEnum.collected.getStatus().equals(collectionScanMarkCounter.getCollectedStatus())
                        ).mapToInt(CollectionScanMarkCounter::getNum).sum();

                    /* 计算是否集齐 */
                    Integer isCollected = collectionScanMarkCounters.parallelStream()
                        .anyMatch(
                            collectionScanMarkCounter ->
                                CollectionStatusEnum.none_collected.getStatus().equals(collectionScanMarkCounter.getCollectedStatus())
                        )? Constants.NUMBER_ZERO : Constants.NUMBER_ONE;

                    /* 计算是否多集 */
                    Integer isExtraCollected = collectionScanMarkCounters.parallelStream()
                        .anyMatch(
                            collectionScanMarkCounter ->
                                CollectionStatusEnum.extra_collected.getStatus().equals(collectionScanMarkCounter.getCollectedStatus())
                        )? Constants.NUMBER_ONE  : Constants.NUMBER_ZERO;

                    CollectionRecordPo collectionRecordPo = new CollectionRecordPo();
                    collectionRecordPo.setCollectionCode(collectionCode);
                    collectionRecordPo.setAggCode(aggCode);
                    collectionRecordPo.setAggCodeType(aggCodeTypeEnum.name());
                    collectionRecordPo.setIsCollected(isCollected);
                    collectionRecordPo.setIsExtraCollected(isExtraCollected);
                    collectionRecordPo.setAggMark(collectionCreatorEntity.getCollectionAggMarks().get(aggCode));
                    collectionRecordPo.setSum(sum);
                    if (collectionRecordDao.updateCollectionRecord(collectionRecordPo) == 0) {
                        collectionRecordDao.insertCollectionRecord(collectionRecordPo);
                    }

                });

            });

        return true;
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
        String collectedMark = collectionCollectorEntity.getCollectionScanCodeEntity().getCollectedMark();
        Map<CollectionAggCodeTypeEnum, String> element = collectionCollectorEntity.getCollectionScanCodeEntity().getCollectionAggCodeMaps();
        if (MapUtils.isEmpty(element)) {
            element = new HashMap<>();
        }
        /* 根据condition去business_code_attribute表中查询所有的集合ID,使用kv_index做查询 */
        List<CollectionCodeEntity> codeEntities = collectionCollectorEntity.genCollectionCodeEntities().parallelStream()
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
            })
            .filter(collectionCodeEntity -> StringUtils.isNotEmpty(collectionCodeEntity.getCollectionCode()))
            .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(codeEntities)) {
                codeEntities = collectionCodeEntities.parallelStream()
                    .filter(collectionCodeEntity -> StringUtils.isNotEmpty(collectionCodeEntity.getCollectionCondition()))
                    .peek(collectionCodeEntity ->
                        collectionCodeEntity.setCollectionCode(getJQCodeByBusinessType(collectionCodeEntity, "none"))
                    ).collect(Collectors.toList());
            }

            Map<CollectionAggCodeTypeEnum, String> finalElement = element;
            codeEntities.forEach(collectionCodeEntity -> {
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
                            .aggCode(finalElement.getOrDefault(aggCodeTypeEnum, "null"))
                            .aggCodeType(aggCodeTypeEnum.name())
                            .collectedStatus(CollectionStatusEnum.extra_collected.getStatus())
                            .collectedMark(collectedMark)
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
                            condition.setSum(Constants.NUMBER_ZERO);
                            condition.setIsExtraCollected(Constants.NUMBER_ONE);
                            condition.setAggMark("");
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
                        .parallelStream().filter(finalElement::containsKey).collect(Collectors.toList());
                    List<CollectionAggCodeTypeEnum> notExistAggCodeTypeEnums = collectionCodeEntity.getBusinessType().getCollectionAggCodeTypes()
                        .parallelStream().filter(aggCodeTypeEnum -> !finalElement.containsKey(aggCodeTypeEnum)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(existAggCodeTypeEnums)) {
                        existAggCodeTypeEnums.forEach(aggCodeTypeEnum ->
                            collectionRecordDao.updateCollectionRecordDetail(CollectionRecordDetailPo.builder()
                                .collectionCode(collectionCodeEntity.getCollectionCode())
                                .scanCode(scanCode)
                                .aggCode(finalElement.getOrDefault(aggCodeTypeEnum, "null"))//此处不会出现null值，因为之前已经进行过filter过滤
                                .aggCodeType(aggCodeTypeEnum.name())
                                .collectedStatus(CollectionStatusEnum.collected.getStatus())
                                .collectedMark(collectedMark)
                                .collectedTime(new Date())
                                .build()));
                    }
                    if (CollectionUtils.isNotEmpty(notExistAggCodeTypeEnums)) {
                        notExistAggCodeTypeEnums.forEach(aggCodeTypeEnum -> {
                            collectionRecordDao.updateCollectionRecordDetail(CollectionRecordDetailPo.builder()
                                .collectionCode(collectionCodeEntity.getCollectionCode())
                                .scanCode(scanCode)
                                .aggCodeType(aggCodeTypeEnum.name())
                                .aggCode(finalElement.getOrDefault(aggCodeTypeEnum, "null"))//此处不会出现null值，因为之前已经进行过filter过滤
                                .collectedStatus(CollectionStatusEnum.collected.getStatus())
                                .collectedMark(collectedMark)
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
                        List<CollectionScanMarkCounter> collectionScanMarkCounters = collectionRecordDao.sumAggCollectedByAggCode(
                            CollectionRecordDetailPo.builder()
                                .collectionCode(collectionCodeEntity.getCollectionCode())
                                .aggCode(aggCodeDetailPo.getAggCode())
                                .aggCodeType(aggCodeDetailPo.getAggCodeType())
                                .build());
                        /* 如果是已经集齐的aggCode的话，需要更新主标的aggCode的信息 */
                        if (null == collectionScanMarkCounters) {
                            log.error("数据查询严重不一致，需要检查逻辑，集齐单号为：{}，聚合号为：{}", collectionCodeEntity.getCollectionCode(), aggCodeDetailPo.getAggCode());
                            return;
                        }

                        //计算总数
                        Integer sum = collectionScanMarkCounters.parallelStream()
                            .filter(
                                collectionScanMarkCounter ->
                                    CollectionStatusEnum.none_collected.getStatus().equals(collectionScanMarkCounter.getCollectedStatus())
                                        || CollectionStatusEnum.collected.getStatus().equals(collectionScanMarkCounter.getCollectedStatus())
                            ).mapToInt(CollectionScanMarkCounter::getNum).sum();

                        /* 计算是否集齐 */
                        Integer isCollected = collectionScanMarkCounters.parallelStream()
                            .anyMatch(
                                collectionScanMarkCounter ->
                                    CollectionStatusEnum.none_collected.getStatus().equals(collectionScanMarkCounter.getCollectedStatus())
                            )? Constants.NUMBER_ZERO : Constants.NUMBER_ONE;

                        /* 计算是否多集 */
                        Integer isExtraCollected = collectionScanMarkCounters.parallelStream()
                            .anyMatch(
                                collectionScanMarkCounter ->
                                    CollectionStatusEnum.extra_collected.getStatus().equals(collectionScanMarkCounter.getCollectedStatus())
                            )? Constants.NUMBER_ONE  : Constants.NUMBER_ZERO;

                        CollectionRecordPo recordCondition = new CollectionRecordPo();
                        recordCondition.setCollectionCode(collectionCodeEntity.getCollectionCode());
                        recordCondition.setAggCode(aggCodeDetailPo.getAggCode());
                        recordCondition.setAggCodeType(aggCodeDetailPo.getAggCodeType());
                        recordCondition.setIsCollected(isCollected);
                        recordCondition.setIsExtraCollected(isExtraCollected);
                        recordCondition.setSum(sum);
                        collectionRecordDao.updateCollectionRecord(recordCondition);

                    });
                }
            });

        return true;
    }

    @Override
    public CollectionAggCodeCounter countCollectionStatusByAggCodeAndCollectionCodeWithCollectedMark(List<CollectionCodeEntity> collectionCodeEntities, String aggCode,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark) {


        List<CollectionAggCodeCounter> aggCodeCounters =
            new ArrayList<>(
                this.sumCollectionByCollectionCodeAndStatus(collectionCodeEntities, null, CollectionAggCodeTypeEnum.waybill_code, aggCode, collectedMark, 10, 0)
            );

        if (CollectionUtils.isEmpty(aggCodeCounters)) {
            return null;
        }

        if (aggCodeCounters.size() == 1) {
             return aggCodeCounters.get(0);
        }

        if (aggCodeCounters.parallelStream().allMatch(collectionAggCodeCounter ->
            collectionAggCodeCounter.getCollectedNum() == 0 && collectionAggCodeCounter.getNoneCollectedNum() == 0)) {
            return aggCodeCounters.parallelStream().findAny().orElse(null);
        } else {
            return aggCodeCounters.parallelStream().filter(collectionAggCodeCounter ->
                collectionAggCodeCounter.getCollectedNum() > 0 || collectionAggCodeCounter.getNoneCollectedNum() > 0).findFirst().orElse(null);
        }


    }

    @Override
    public Integer countNoneCollectedAggCodeNumByCollectionCode(List<CollectionCodeEntity> collectionCodeEntities, CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark) {
        if (CollectionUtils.isEmpty(collectionCodeEntities)) {
            return 0;
        }

        List<String> collectionCodes = collectionCodeEntities.parallelStream().map(CollectionCodeEntity::getCollectionCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collectionCodes)) {
            return 0;
        }

        return collectionRecordDao.countNoneCollectedAggCodeByCollectionCodeWithCollectedMark(
            collectionCodes, aggCodeTypeEnum, collectedMark);

    }

    @Override
    public List<CollectionCounter> sumCollectionByCollectionCode(List<CollectionCodeEntity> collectionCodeEntities, CollectionAggCodeTypeEnum aggCodeTypeEnum) {
        if (CollectionUtils.isEmpty(collectionCodeEntities)) {
            log.info("待查询的集合ID不存在，查询失败");
            return Collections.emptyList();
        }

        List<String> collectionCodes = collectionCodeEntities.parallelStream()
            .map(CollectionCodeEntity::getCollectionCode)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(collectionCodes)) {
            return Collections.emptyList();
        }

        List<CollectionCounter> collectionCounters = collectionRecordDao.sumCollectionRecordByCollectionCode(collectionCodes, aggCodeTypeEnum);

        return collectionCounters.parallelStream()
            .peek(
                collectionCounter ->
                    collectionCounter.setNoneCollectedNum(collectionCounter.getSumScanNum() - collectionCounter.getCollectedNum())
            )
            .collect(Collectors.toList());

    }

    @Override
    public List<CollectionAggCodeCounter> sumCollectionByCollectionCodeAndStatus(
        List<CollectionCodeEntity> collectionCodeEntities, CollectionStatusEnum collectionStatusEnum,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode, String collectedMark, Integer limit, Integer offset) {
        if (CollectionUtils.isEmpty(collectionCodeEntities)) {
            return Collections.emptyList();
        }

        List<String> collectionCodes = collectionCodeEntities.parallelStream()
            .map(CollectionCodeEntity::getCollectionCode)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(collectionCodes)) {
            return Collections.emptyList();
        }

        List<CollectionCollectedMarkCounter> collectionCollectedMarkCounters =
            collectionRecordDao.sumCollectionAggCodeByCollectionCode(collectionCodes,
                CollectionStatusEnum.collected.equals(collectionStatusEnum)? Constants.NUMBER_ONE : null,
                aggCode, aggCodeTypeEnum, limit, offset);

        if (CollectionUtils.isEmpty(collectionCollectedMarkCounters)) {
            return Collections.emptyList();
        }

        Map<String, List<CollectionCollectedMarkCounter>> markCounterMap = collectionCollectedMarkCounters.parallelStream().collect(Collectors.groupingBy(
            collectionCollectedMarkCounter -> collectionCollectedMarkCounter.getCollectionCode()
                .concat(",")
                .concat(collectionCollectedMarkCounter.getAggCode())
                .concat(",")
                .concat(collectionCollectedMarkCounter.getAggCodeType())));

        Map<String, List<CollectionCollectedMarkCounter>> effectiveMarkCounterMap = markCounterMap;

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
                            item -> CollectionStatusEnum.none_collected.getStatus().equals(item.getCollectedStatus()) && StringUtils.isEmpty(item.getCollectedMark())
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

        return collectionAggCodeCounters;
    }

    @Override
    public List<CollectionScanCodeDetail> queryCollectionScanDetailByAggCode(
        List<CollectionCodeEntity> collectionCodeEntities, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark, Integer limit, Integer offset) {

        if (CollectionUtils.isEmpty(collectionCodeEntities) || StringUtils.isEmpty(aggCode) || null == aggCodeTypeEnum) {
            return Collections.emptyList();
        }

        List<String> collectionCodes = collectionCodeEntities.parallelStream().map(CollectionCodeEntity::getCollectionCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collectionCodes)) {
            return Collections.emptyList();
        }

        List<CollectionRecordDetailPo> collectionRecordDetailPos = collectionRecordDao.queryCollectedDetailByCollectionAndAggCode(
            collectionCodes, aggCode, aggCodeTypeEnum, limit, offset);

        if (CollectionUtils.isEmpty(collectionRecordDetailPos)) {
            return Collections.emptyList();
        }

        return collectionRecordDetailPos.parallelStream().map(
            collectionRecordDetailPo -> CollectionScanCodeDetail.builder()
                .collectionCode(collectionRecordDetailPo.getCollectionCode())
                .aggCode(collectionRecordDetailPo.getAggCode())
                .aggCodeType(collectionRecordDetailPo.getAggCodeType())
                .scanCode(collectionRecordDetailPo.getScanCode())
                .scanCodeType(collectionRecordDetailPo.getScanCodeType())
                .collectedStatus(CollectionStatusEnum.getEnum(collectionRecordDetailPo.getCollectedStatus()))
                .collectedMarkType(
                    StringUtils.isEmpty(collectionRecordDetailPo.getCollectedMark())?
                        CollectionCollectedMarkTypeEnum.none :
                        Objects.equals(collectionRecordDetailPo.getCollectedMark(), collectedMark)?
                            CollectionCollectedMarkTypeEnum.inner : CollectionCollectedMarkTypeEnum.outer
                )
                .build()
            )
            .collect(Collectors.toList());

    }
}
