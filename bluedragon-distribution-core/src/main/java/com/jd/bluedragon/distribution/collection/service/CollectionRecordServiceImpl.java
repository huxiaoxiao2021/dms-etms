package com.jd.bluedragon.distribution.collection.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.busineCode.jqCode.JQCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.collection.builder.CollectionEntityConverter;
import com.jd.bluedragon.distribution.collection.dao.CollectionRecordDao;
import com.jd.bluedragon.distribution.collection.entity.*;
import com.jd.bluedragon.distribution.collection.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.fastjson.JSON;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
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
    private JimDbLock jimDbLock;

    @Autowired
    private KvIndexDao kvIndexDao;

    @Autowired
    private JQCodeService jqCodeService;

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.getOrGenJQCodeByBusinessType", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public String getOrGenJQCodeByBusinessType(CollectionCodeEntity collectionCodeEntity, String userErp) {
        String methodDesc =  "CollectionRecordServiceImpl.getJQCodeByBusinessType:获取collectionCode:";
        if (null == collectionCodeEntity || null == collectionCodeEntity.getBusinessType()) {
            log.error("获取待集齐集合ID失败，参数错误：{}", JSON.toJSONString(collectionCodeEntity));
            return "";
        }
        String condition = collectionCodeEntity.buildCollectionCondition().getCollectionCondition();
        if (StringUtils.isBlank(condition)) {
            log.error("获取待集齐集合ID的要素失败，参数错误：{}", JSON.toJSONString(collectionCodeEntity));
            return "";
        }
        if(log.isInfoEnabled()) {
            log.info("{},参数={},erp={},condition={}", methodDesc, JSON.toJSONString(collectionCodeEntity), userErp, condition);
        }
        String JQCode = kvIndexDao.queryRecentOneByKeyword(condition.toUpperCase());
        if (StringUtils.isNotEmpty(JQCode)) {
            log.info("获取到已经创建的待集齐集合ID[{}],参数为:{}，condition={}", JQCode, JSON.toJSONString(collectionCodeEntity), condition);
            return JQCode;
        }
        JQCode = jqCodeService.createJQCode(collectionCodeEntity.getCollectElements(), collectionCodeEntity.getBusinessType(),
            BusinessCodeFromSourceEnum.DMS_WORKER_SYS, null == userErp? "none" : userErp);
        if(log.isInfoEnabled()) {
            log.info("{},生成collectionCode成功；参数={},erp={},condition={}, JQCode={}", methodDesc, JSON.toJSONString(collectionCodeEntity), userErp, condition, JQCode);
        }
        return JQCode;
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.queryAllCollectionCodesByElement", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<CollectionCodeEntity> queryAllCollectionCodesByElement(Map<CollectionConditionKeyEnum, Object> elements,
        CollectionBusinessTypeEnum businessType) {

        return Arrays.stream(CollectionBusinessTypeEnum.values())
            .filter(
                businessTypeEnum -> null == businessType || businessTypeEnum.equals(businessType)
            )
            .flatMap((Function<CollectionBusinessTypeEnum, Stream<CollectionCodeEntity>>)businessTypeEnum -> {
                CollectionCodeEntity collectionCodeEntity = CollectionEntityConverter.buildCollectionCodeEntity(elements, businessTypeEnum, null);
                List<String> jqCodes = kvIndexDao.queryByKeyword(collectionCodeEntity.getCollectionCondition().toUpperCase());
                if (CollectionUtils.isEmpty(jqCodes)) {
                    return Stream.empty();
                }
                return jqCodes.stream().map(jqCode -> {
                    log.info("根据condition:{}命中了待集齐集合ID:{}",collectionCodeEntity.getCollectionCondition(), jqCode);
                    return CollectionEntityConverter.buildCollectionCodeEntity(elements, businessTypeEnum, jqCode);
                });
            }).collect(
                Collectors.collectingAndThen(
                    Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(CollectionCodeEntity::getCollectionCode))), ArrayList::new
                )
            );
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

                    /* 对包裹列表数进行分页处理 */
                    Lists.partition(collectionRecordDetailPos, Constants.DEFAULT_PAGE_SIZE).forEach(partitionDetailPos -> {
                        /* 检查当前这批数据下的明细表的数据是否已经存在，在增量模式下，不会对已有的数据造成影响 */
                        //todo zcf 查明细
                        List<CollectionRecordDetailPo> collectionRecordDetailPosExist =
                            collectionRecordDao.findExistDetails(collectionCode,
                                partitionDetailPos.parallelStream().map(CollectionRecordDetailPo::getScanCode)
                                    .collect(Collectors.toList()), aggCode, aggCodeTypeEnum);

                        Map<String, List<CollectionRecordDetailPo>> collectionRecordDetailPosExistMap =
                            collectionRecordDetailPosExist.parallelStream().collect(Collectors.groupingBy(CollectionRecordDetailPo::getScanCode));

                        /* 如果待初始化的信息，已经在待集齐集合中存在，那么需要根据已经存在的信息选择更新状态或者是skip，分两拨处理 */
                        List<CollectionRecordDetailPo> collectionRecordDetailPosNotExist = partitionDetailPos.parallelStream().filter(
                            /* 首选过滤出不存在的情况 */
                            collectionRecordDetailPo -> !collectionRecordDetailPosExistMap.containsKey(collectionRecordDetailPo.getScanCode())
                        ).collect(Collectors.toList());
                        /* 新增到数据库中 */
                        //todo zcf 插入
                        if (CollectionUtils.isNotEmpty(collectionRecordDetailPosNotExist)) {
                            Integer barthInsertNum = collectionRecordDao.batchInsertCollectionRecordDetail(collectionRecordDetailPosNotExist);
                            if(log.isInfoEnabled()) {
                                log.info("initPartCollection:批量插入明细数量barthInsertNum={},collectionCode={},aggCode={}", barthInsertNum,collectionCode, aggCode);
                            }
                        }

                        /* 在过滤出已经存在的情况下，且状态是多扫的状态下 */
                        List<CollectionRecordDetailPo> collectionRecordDetailPosExtraExist = partitionDetailPos.parallelStream().filter(
                            /* 首选过滤出存在且标注多扫的状态 */
                            collectionRecordDetailPo ->
                                collectionRecordDetailPosExistMap.containsKey(collectionRecordDetailPo.getScanCode()) &&
                                    collectionRecordDetailPosExistMap.get(collectionRecordDetailPo.getScanCode()).parallelStream().anyMatch(
                                        collectionRecordDetailPo1 -> CollectionStatusEnum.extra_collected.getStatus().equals(
                                            collectionRecordDetailPo1.getCollectedStatus())
                                    )
                        ).collect(Collectors.toList());
                        /* 更新到数据库中 */
                        //todo zcf 修改
                        if (CollectionUtils.isNotEmpty(collectionRecordDetailPosExtraExist)) {
                            Integer barthUpdateNum =collectionRecordDao.updateDetailInfoByScanCodes(collectionCode,
                                collectionRecordDetailPosExtraExist.parallelStream().map(CollectionRecordDetailPo::getScanCode)
                                    .collect(Collectors.toList()), aggCode, aggCodeTypeEnum, CollectionStatusEnum.collected,null);
                            if(log.isInfoEnabled()) {
                                log.info("initPartCollection:批量修改明细数量barthInsertNum={},collectionCode={},aggCode={}", barthUpdateNum, collectionCode, aggCode);
                            }
                        }
                    });

                    String collectedMark = collectionRecordDetailPos.parallelStream()
                        .map(CollectionRecordDetailPo::getCollectedMark)
                        .filter(StringUtils::isNotEmpty)
                        .findAny().orElse("");

                    String key = MessageFormat.format(Constants.JQ_AGG_LOCK_PREFIX, collectionCode,aggCodeTypeEnum.name(),aggCode);
                    if (jimDbLock.lock(key,"1", 10L, TimeUnit.MINUTES)) {
                        //todo zcf 统计明细数据查询
                        CollectionAggCodeCounter collectionAggCodeCounter = this.sumCollectionByAggCodeAndCollectionCode(
                            collectionCreatorEntity.getCollectionCodeEntity(), aggCode, aggCodeTypeEnum, collectedMark);
                        if(log.isInfoEnabled()) {
                            log.info("initPartCollection:集齐服务统计数据结果集转换={},param=【{}|{}|{}|{}】", JsonUtils.toJSONString(collectionAggCodeCounter),
                                    collectionCreatorEntity.getCollectionCodeEntity(), aggCode, aggCodeTypeEnum, collectedMark);
                        }
                        CollectionRecordPo collectionRecordPo = new CollectionRecordPo();
                        collectionRecordPo.setCollectionCode(collectionCode);
                        collectionRecordPo.setAggCode(aggCode);
                        collectionRecordPo.setAggCodeType(aggCodeTypeEnum.name());
                        collectionRecordPo.setInitNumber(collectionAggCodeCounter.getSumScanNum());
                        collectionRecordPo.setIsInit(Constants.NUMBER_ONE);
                        collectionRecordPo.setIsCollected(collectionAggCodeCounter.getCollectedNum() > 0 && collectionAggCodeCounter.getNoneCollectedNum() == 0?
                            Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                        collectionRecordPo.setIsExtraCollected(collectionAggCodeCounter.getExtraCollectedNum() > 0?
                            Constants.NUMBER_ONE : Constants.NUMBER_ZERO);

                        if (StringUtils.isNotEmpty(collectedMark)) {
                            collectionRecordPo.setIsMoreCollectedMark(collectionAggCodeCounter.getOutMarkCollectedNum()>0?
                                Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                        }
                        collectionRecordPo.setAggMark(collectionCreatorEntity.getCollectionAggMarks().getOrDefault(aggCode,""));
                        //todo zcf 插入删除
                        if(log.isInfoEnabled()) {
                            log.info("initPartCollection：集齐主表统计插入修改collectionRecordPo={}", JsonUtils.toJSONString(collectionRecordPo));
                        }
                        if (collectionRecordDao.updateCollectionRecord(collectionRecordPo) <= 0) {
                            collectionRecordPo.setIsMoreCollectedMark(Constants.NUMBER_ZERO);
                            Integer insertNum = collectionRecordDao.insertCollectionRecord(collectionRecordPo);
                            if(insertNum <= 0 && log.isInfoEnabled()) {
                                log.info("initPartCollection：集齐主表统计插入数量为0，collectionRecordPo={}", JsonUtils.toJSONString(collectionRecordPo));
                            }
                        }
                        jimDbLock.releaseLock(key, "1");
                    }
                });
            });

        return true;
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.initAndCollectedPartCollection", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
                    CollectionEntityConverter.getCollectedDetailPoFromCollectionCreatorByAggCode(collectionCreatorEntity, aggCodeTypeEnum);

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

                    /* 对包裹列表数进行分页处理 */
                    Lists.partition(collectionRecordDetailPos, Constants.DEFAULT_PAGE_SIZE).forEach(partitionDetailPos -> {
                        /* 检查当前这批数据下的明细表的数据是否已经存在，在增量模式下，不会对已有的数据造成影响 */
                        //todo zcf 查明细
                        List<CollectionRecordDetailPo> collectionRecordDetailPosExist =
                            collectionRecordDao.findExistDetails(collectionCode,
                                partitionDetailPos.parallelStream().map(CollectionRecordDetailPo::getScanCode)
                                    .collect(Collectors.toList()), aggCode, aggCodeTypeEnum);

                        Map<String, List<CollectionRecordDetailPo>> collectionRecordDetailPosExistMap =
                            collectionRecordDetailPosExist.parallelStream().collect(Collectors.groupingBy(CollectionRecordDetailPo::getScanCode));

                        /* 如果待初始化的信息，已经在待集齐集合中存在，那么需要根据已经存在的信息选择更新状态或者是skip，分两拨处理 */
                        List<CollectionRecordDetailPo> collectionRecordDetailPosNotExist = partitionDetailPos.parallelStream().filter(
                            /* 首选过滤出不存在的情况 */
                            collectionRecordDetailPo -> !collectionRecordDetailPosExistMap.containsKey(collectionRecordDetailPo.getScanCode())
                        ).collect(Collectors.toList());
                        /* 新增到数据库中 */
                        if (CollectionUtils.isNotEmpty(collectionRecordDetailPosNotExist)) {
                            Integer batchInsertNum = collectionRecordDao.batchInsertCollectionRecordDetail(collectionRecordDetailPosNotExist);
                            if(log.isInfoEnabled()) {
                                log.info("initAndCollectedPartCollection:批量插入数量为{}，collectionCode-{},aggCode={}", batchInsertNum, collectionCode, aggCode);
                            }
                        }

                        /* 在过滤出已经存在的情况下，且状态是多扫或者未扫的状态下 */
                        partitionDetailPos.parallelStream()
                            .filter(
                                /* 首选过滤出存在且标注多扫和未扫的状态 */
                                collectionRecordDetailPo ->
                                    collectionRecordDetailPosExistMap.containsKey(collectionRecordDetailPo.getScanCode()) &&
                                        collectionRecordDetailPosExistMap.get(collectionRecordDetailPo.getScanCode()).parallelStream().anyMatch(
                                            collectionRecordDetailPo1 -> !CollectionStatusEnum.collected.getStatus().equals(
                                                collectionRecordDetailPo1.getCollectedStatus())
                                        )
                            )
                            .collect(Collectors.groupingBy(CollectionRecordDetailPo::getCollectedMark))
                            .forEach((collectedMark,itemCollectionRecordDetailPosExtraExist) -> {
                                /* 更新到数据库中 */
                                Integer batchUpdateNum = collectionRecordDao.updateDetailInfoByScanCodes(collectionCode,
                                    itemCollectionRecordDetailPosExtraExist.parallelStream().map(CollectionRecordDetailPo::getScanCode)
                                        .collect(Collectors.toList()), aggCode, aggCodeTypeEnum, CollectionStatusEnum.collected,collectedMark);
                                if(log.isInfoEnabled()) {
                                    log.info("initAndCollectedPartCollection:批量修改数量为{}，collectionCode-{},aggCode={}", batchUpdateNum, collectionCode, aggCode);
                                }
                            });
                    });

                    String key = MessageFormat.format(Constants.JQ_AGG_LOCK_PREFIX, collectionCode,aggCodeTypeEnum.name(),aggCode);
                    if (jimDbLock.lock(key,"1", 10L, TimeUnit.MINUTES)) {

                        String collectedMark = collectionRecordDetailPos.parallelStream()
                                .map(CollectionRecordDetailPo::getCollectedMark)
                                .filter(StringUtils::isNotEmpty)
                                .findAny().orElse("");
                        CollectionAggCodeCounter collectionAggCodeCounter = this.sumCollectionByAggCodeAndCollectionCode(
                            collectionCreatorEntity.getCollectionCodeEntity(),
                            aggCode, aggCodeTypeEnum,collectedMark

                        );
                        if(log.isInfoEnabled()) {
                            log.info("initAndCollectedPartCollection:集齐服务统计数据结果集转换={},param=【{}|{}|{}|{}】", JsonUtils.toJSONString(collectionAggCodeCounter),
                                    collectionCreatorEntity.getCollectionCodeEntity(), aggCode, aggCodeTypeEnum, collectedMark);
                        }
                        CollectionRecordPo collectionRecordPo = new CollectionRecordPo();
                        collectionRecordPo.setCollectionCode(collectionCode);
                        collectionRecordPo.setAggCode(aggCode);
                        collectionRecordPo.setAggCodeType(aggCodeTypeEnum.name());
                        collectionRecordPo.setInitNumber(collectionAggCodeCounter.getSumScanNum());
                        collectionRecordPo.setIsInit(Constants.NUMBER_ONE);
                        collectionRecordPo.setIsCollected(collectionAggCodeCounter.getCollectedNum() > 0 && collectionAggCodeCounter.getNoneCollectedNum() == 0?
                            Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                        collectionRecordPo.setIsExtraCollected(collectionAggCodeCounter.getExtraCollectedNum() > 0?
                            Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                        collectionRecordPo.setIsMoreCollectedMark(collectionAggCodeCounter.getOutMarkCollectedNum()>0?
                            Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                        collectionRecordPo.setAggMark(collectionCreatorEntity.getCollectionAggMarks().getOrDefault(aggCode,""));

                        if(log.isInfoEnabled()) {
                            log.info("initAndCollectedPartCollection:插入或修改集齐主表数据collectionRecordPo={}", JsonUtils.toJSONString(collectionRecordPo));
                        }
                        if (collectionRecordDao.updateCollectionRecord(collectionRecordPo) <= 0) {
                            Integer insertNum = collectionRecordDao.insertCollectionRecord(collectionRecordPo);
                            if(log.isInfoEnabled()) {
                                log.info("initAndCollectedPartCollection:插入集齐主表数据0条，collectionRecordPo={}", JsonUtils.toJSONString(collectionRecordPo));
                            }
                        }
                        jimDbLock.releaseLock(key, "1");
                    }

                });

            });

        return true;
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.collectTheScanCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean collectTheScanCode(CollectionCollectorEntity collectionCollectorEntity, Result<Boolean> result) {
        if (null == collectionCollectorEntity
            || StringUtils.isBlank(collectionCollectorEntity.getCollectionScanCodeEntity().getScanCode())) {
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

        if(log.isInfoEnabled()) {
            log.info("CollectionRecordService.collectTheScanCode单条修改集齐状态：参数={}", JsonUtils.toJSONString(collectionCollectorEntity));
        }
        String scanCode = collectionCollectorEntity.getCollectionScanCodeEntity().getScanCode();
        String scanCodeType = collectionCollectorEntity.getCollectionScanCodeEntity().getScanCodeType().name();
        String collectedMark = collectionCollectorEntity.getCollectionScanCodeEntity().getCollectedMark();
        Map<CollectionAggCodeTypeEnum, String> element = collectionCollectorEntity.getCollectionScanCodeEntity().getCollectionAggCodeMaps();
        if (MapUtils.isEmpty(element)) {
            element = new HashMap<>();
        }
        /* 根据condition去business_code_attribute表中查询所有的集合ID,使用kv_index做查询 */
        List<CollectionCodeEntity> codeEntities = collectionCollectorEntity.genCollectionCodeEntities().parallelStream()
            .filter(collectionCodeEntity -> StringUtils.isNotEmpty(collectionCodeEntity.getCollectionCondition()))
            .peek(codeEntity ->
                codeEntity.setCollectionCode(getCollectionCode(codeEntity, "NONE"))
            )
            .filter(collectionCodeEntity -> StringUtils.isNotEmpty(collectionCodeEntity.getCollectionCode()))//理论上collectionCode不会为空
            .collect(Collectors.toList());

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
                        .scanCodeType(scanCodeType)
                        .aggCode(finalElement.getOrDefault(aggCodeTypeEnum, "null"))
                        .aggCodeType(aggCodeTypeEnum.name())
                        .collectedStatus(CollectionStatusEnum.extra_collected.getStatus())
                        .collectedMark(collectedMark)
                        .createTime(new Date())
                        .build())
                    .collect(Collectors.toList());

                Integer batchInsertNum = collectionRecordDao.batchInsertCollectionRecordDetail(collectionRecordDetailPos);
                if(log.isInfoEnabled()) {
                    log.info("collectTheScanCode:批量插入数量为{},collectionRecordDetailPos(此场景理论只有一条数据)={}",batchInsertNum, JsonUtils.toJSONString(collectionRecordDetailPos));
                }
            }
            if (CollectionUtils.isNotEmpty(scanDetailPos) && CollectionStatusEnum.none_collected.getStatus().equals(scanDetailPos.get(0).getCollectedStatus())) {

                CollectionRecordDetailPo collectionRecordDetailPo = CollectionRecordDetailPo.builder()
                        .collectionCode(collectionCodeEntity.getCollectionCode())
                        .scanCode(scanCode)
                        .collectedStatus(CollectionStatusEnum.collected.getStatus())
                        .collectedMark(collectedMark)
                        .collectedTime(new Date())
                        .build();
                //更新为集齐状态
                Integer batchUpdateNum = collectionRecordDao.updateCollectionRecordDetail(collectionRecordDetailPo);
                if(log.isInfoEnabled()) {
                    log.info("collectTheScanCode:批量插入数量为{}, collectionRecordDetailPo={}",batchUpdateNum, JsonUtils.toJSONString(collectionRecordDetailPo));
                }
            }

            /* 更新主表 */
            List<CollectionRecordDetailPo> aggCodeDetailPos = collectionRecordDao.findAggCodeByScanCode(
                CollectionRecordDetailPo.builder()
                    .collectionCode(collectionCodeEntity.getCollectionCode())
                    .scanCode(scanCode)
                    .build()
            );

            aggCodeDetailPos.forEach(aggCodeDetailPo -> {

                    String key = MessageFormat.format(Constants.JQ_AGG_LOCK_PREFIX, collectionCodeEntity.getCollectionCode(),aggCodeDetailPo.getAggCodeType(),aggCodeDetailPo.getAggCode());
                    if (jimDbLock.lock(key,"1", 10L, TimeUnit.MINUTES)) {

                        /* 根据collectionCode， aggCodeType， aggCode， 查询是否集齐 */
                        CollectionAggCodeCounter collectionAggCodeCounter = this.sumCollectionByAggCodeAndCollectionCode(
                            collectionCodeEntity,aggCodeDetailPo.getAggCode(),
                            CollectionAggCodeTypeEnum.valueOf(aggCodeDetailPo.getAggCodeType()), collectedMark
                        );
                        if(log.isInfoEnabled()) {
                            log.info("collectTheScanCode:集齐服务统计数据结果集转换={},param=【{}|{}|{}|{}】", JsonUtils.toJSONString(collectionAggCodeCounter),
                                    collectionCodeEntity, aggCodeDetailPo.getAggCode(), CollectionAggCodeTypeEnum.valueOf(aggCodeDetailPo.getAggCodeType()), collectedMark);
                        }
                        CollectionRecordPo collectionRecordPo = new CollectionRecordPo();
                        collectionRecordPo.setCollectionCode(collectionCodeEntity.getCollectionCode());
                        collectionRecordPo.setAggCode(aggCodeDetailPo.getAggCode());
                        collectionRecordPo.setAggCodeType(aggCodeDetailPo.getAggCodeType());
                        collectionRecordPo.setInitNumber(collectionAggCodeCounter.getSumScanNum());
                        collectionRecordPo.setIsCollected(collectionAggCodeCounter.getCollectedNum() > 0 && collectionAggCodeCounter.getNoneCollectedNum() == 0?
                            Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                        collectionRecordPo.setIsExtraCollected(collectionAggCodeCounter.getExtraCollectedNum() > 0?
                            Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                        if(StringUtils.isNotBlank(collectionCollectorEntity.getAggMark())) {
                            collectionRecordPo.setAggMark(collectionCollectorEntity.getAggMark());
                        }
                        if (collectionAggCodeCounter.getOutMarkCollectedNum() > 0 && collectionAggCodeCounter.getInnerMarkCollectedNum() > 0) {
                            collectionRecordPo.setIsMoreCollectedMark(Constants.NUMBER_ONE);
                        }
                        log.info("collectTheScanCode:主表插入修改collectionRecordPo{}", JsonUtils.toJSONString(collectionRecordPo));
                        if (collectionRecordDao.updateCollectionRecord(collectionRecordPo) <= 0) {
                            collectionRecordPo.setIsMoreCollectedMark(Constants.NUMBER_ZERO);
                            collectionRecordPo.setIsInit(Constants.NUMBER_ZERO);
                            Integer insertNum = collectionRecordDao.insertCollectionRecord(collectionRecordPo);
                            if(insertNum <= 0 && log.isInfoEnabled()) {
                                log.info("collectTheScanCode:主表插入数量为0，collectionRecordPo{}", JsonUtils.toJSONString(collectionRecordPo));
                            }
                        }
                        jimDbLock.releaseLock(key, "1");

                    }
            });
        });

        return true;
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.sumCollectionByAggCodeAndCollectionCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<CollectionAggCodeCounter> sumCollectionByAggCodeAndCollectionCode(List<CollectionCodeEntity> collectionCodeEntities, CollectionCodeEntity importCollectionCodeEntity,
        String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark) {
        List<String> collectionCodes = CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(collectionCodeEntities);
        if (CollectionUtils.isEmpty(collectionCodes) || StringUtils.isBlank(aggCode) || aggCodeTypeEnum == null || StringUtils.isBlank(collectedMark)) {
            log.warn("根据aggCode查询集齐统计情况，参数不正确，请检查:{}-{}-{}-{}", JSON.toJSONString(collectionCodeEntities), aggCode, aggCodeTypeEnum, collectedMark);
            return null;
        }
        if(log.isInfoEnabled()) {
            log.info("sumCollectionByAggCodeAndCollectionCode:start:参数【{}|{}|{}|{}|{}】",
                    JsonUtils.toJSONString(collectionCodeEntities), JsonUtils.toJSONString(importCollectionCodeEntity), aggCode, JsonUtils.toJSONString(aggCodeTypeEnum), collectedMark);
        }
        List<CollectionCodeEntity> codeEntities = collectionCodeEntities.parallelStream().filter(collectionCodeEntity -> {
            List<CollectionRecordPo> collectionRecordPos = collectionRecordDao.findCollectionRecord(
                CollectionRecordPo.builder()
                    .collectionCode(collectionCodeEntity.getCollectionCode())
                    .isInit(Constants.NUMBER_ONE)
                    .aggCode(aggCode)
                    .aggCodeType(aggCodeTypeEnum.name())
                    .build());
            return CollectionUtils.isNotEmpty(collectionRecordPos);
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(codeEntities)) {
            log.warn("本次在多个池子中查询的数据，都是未进行初始化的数据，数据将使用兜底的场地的数据进行查询");
            codeEntities.add(importCollectionCodeEntity);
        }
        collectionCodes = CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(codeEntities);
        if(log.isInfoEnabled()) {
            log.info("sumCollectionByAggCodeAndCollectionCode拿到collectionCode为{}，参数={}", JsonUtils.toJSONString(collectionCodes), JsonUtils.toJSONString(codeEntities));
        }
        List<CollectionCollectedMarkCounter> collectionCollectedMarkCounters = collectionRecordDao.countCollectionByAggCodeAndCollectionCodes(collectionCodes, aggCode, aggCodeTypeEnum);
        if (CollectionUtils.isEmpty(collectionCollectedMarkCounters)) {
            log.error("根据aggCode查询集齐统计情况失败，参数为：{}-{}-{}",JSON.toJSONString(collectionCodes),aggCode, aggCodeTypeEnum);
            return null;
        }
        Map<String, CollectionBusinessTypeEnum> map = codeEntities.parallelStream().collect(Collectors.toMap(CollectionCodeEntity::getCollectionCode, CollectionCodeEntity::getBusinessType));

        collectionCollectedMarkCounters.forEach(collectionCollectedMarkCounter -> {
            collectionCollectedMarkCounter.setBusinessType(map.get(collectionCollectedMarkCounter.getCollectionCode()));
            collectionCollectedMarkCounter.setAggCode(aggCode);
            collectionCollectedMarkCounter.setAggCodeType(aggCodeTypeEnum.name());
        });

        return CollectionEntityConverter
            .convertCollectionCollectedMarkCounterToCollectionAggCodeCounter(collectionCollectedMarkCounters, collectedMark);

    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.sumCollectionByAggCodeAndCollectionCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public CollectionAggCodeCounter sumCollectionByAggCodeAndCollectionCode(CollectionCodeEntity collectionCodeEntity,
        String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark) {

        String collectionCode = collectionCodeEntity.getCollectionCode();
        if (StringUtils.isBlank(collectionCode) || StringUtils.isBlank(aggCode) || aggCodeTypeEnum == null) {
            log.warn("根据aggCode查询集齐统计情况，参数不正确，请检查:{}-{}-{}-{}", JSON.toJSONString(collectionCodeEntity), aggCode, aggCodeTypeEnum, collectedMark);
            return null;
        }

        List<CollectionCollectedMarkCounter> collectionCollectedMarkCounters = collectionRecordDao
            .countCollectionByAggCodeAndCollectionCodes(Collections.singletonList(collectionCode), aggCode, aggCodeTypeEnum);

        if (CollectionUtils.isEmpty(collectionCollectedMarkCounters)) {
            log.error("根据aggCode查询集齐统计情况失败，参数为：{}-{}-{}",JSON.toJSONString(collectionCode),aggCode, aggCodeTypeEnum);
            return null;
        }

        collectionCollectedMarkCounters.forEach(collectionCollectedMarkCounter -> {
            collectionCollectedMarkCounter.setBusinessType(collectionCodeEntity.getBusinessType());
            collectionCollectedMarkCounter.setAggCode(aggCode);
            collectionCollectedMarkCounter.setAggCodeType(aggCodeTypeEnum.name());
        });

        List<CollectionAggCodeCounter> collectionAggCodeCounters = CollectionEntityConverter
            .convertCollectionCollectedMarkCounterToCollectionAggCodeCounter(collectionCollectedMarkCounters, collectedMark);

        return collectionAggCodeCounters.parallelStream().findAny().orElse(null);
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.countNoneCollectedAggCodeNumByCollectionCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Integer countNoneCollectedAggCodeNumByCollectionCode(List<CollectionCodeEntity> collectionCodeEntities,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode, String collectedMark) {
        if (CollectionUtils.isEmpty(collectionCodeEntities)) {
            return 0;
        }

        List<String> collectionCodes = collectionCodeEntities.parallelStream().map(CollectionCodeEntity::getCollectionCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collectionCodes)) {
            return 0;
        }

        return collectionRecordDao.countAggCodeByCollectionCodesAndStatus(collectionCodes, aggCodeTypeEnum,
         collectedMark, Constants.NUMBER_ZERO, null);

    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.countCollectionAggCodeNumByCollectionCodeInnerMark", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Integer countCollectionAggCodeNumByCollectionCodeInnerMark(List<CollectionCodeEntity> collectionCodeEntities,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode, String collectedMark) {

        List<String> collectionCodes = CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(collectionCodeEntities);

        return collectionRecordDao.countAggCodeByCollectionCodesAndStatus(collectionCodes, aggCodeTypeEnum,
            collectedMark, Constants.NUMBER_ONE, Constants.NUMBER_ZERO);

    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.countCollectionAggCodeNumByCollectionCodeOutMark", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Integer countCollectionAggCodeNumByCollectionCodeOutMark(List<CollectionCodeEntity> collectionCodeEntities,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode, String collectedMark) {

        List<String> collectionCodes = CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(collectionCodeEntities);

        return collectionRecordDao.countAggCodeByCollectionCodesAndStatus(collectionCodes, aggCodeTypeEnum,
            collectedMark, Constants.NUMBER_ONE, Constants.NUMBER_ONE);
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.sumNoneCollectedAggCodeByCollectionCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<CollectionAggCodeCounter> sumNoneCollectedAggCodeByCollectionCode(
        List<CollectionCodeEntity> collectionCodeEntities, CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode,
        String collectedMark, Integer limit, Integer offset) {

        if (CollectionUtils.isEmpty(collectionCodeEntities) || null == aggCodeTypeEnum || StringUtils.isBlank(collectedMark)) {
            return Collections.emptyList();
        }
        List<String> collectionCodes = CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(collectionCodeEntities);
        if (CollectionUtils.isEmpty(collectionCodes)) {
            return Collections.emptyList();
        }

        List<CollectionRecordPo> collectionRecordPos = collectionRecordDao.findAggCodeByCollectedMark(collectionCodes, collectedMark, aggCodeTypeEnum, aggCode,
            0, null, null, limit, offset);

        if (CollectionUtils.isEmpty(collectionRecordPos)) {
            return Collections.emptyList();
        }
        List<String> aggCodes = collectionRecordPos.parallelStream().map(CollectionRecordPo::getAggCode)
            .collect(Collectors.toList());
        Map<String, String> aggMarkMap = collectionRecordPos.stream().filter(collectionRecordPo -> StringUtils.isNotBlank(collectionRecordPo.getAggMark()))
            .collect(Collectors.toMap(CollectionRecordPo::getAggCode, CollectionRecordPo::getAggMark, (s, s2) -> s2));

        List<CollectionAggCodeCounter> collectionAggCodeCounterList = this.sumAggCollectionByCollectionCode(collectionCodeEntities,aggCodes, aggCodeTypeEnum,collectedMark);

        List<CollectionAggCodeCounter> res = new ArrayList<>();
        Map<String, CollectionAggCodeCounter> map = getCollectionAggCodeCounterMap(collectionAggCodeCounterList, aggMarkMap);
        for(CollectionRecordPo pojo : collectionRecordPos) {
            String key = getKey(pojo.getCollectionCode(), pojo.getAggCode(), aggCodeTypeEnum.name());
            CollectionAggCodeCounter collectionAggCodeCounter = map.get(key);
            if(Objects.isNull(collectionAggCodeCounter)) {
                log.warn("集齐服务根据主表查询附表信息，没有查到附表统计数据，应该是存在主表明细表数据不一致，主表查到，明细表没查到或者查到加工完之后数据缺失，key={}", key);
            }
            res.add(collectionAggCodeCounter);
        }
        return res;
    }

    private Map<String, CollectionAggCodeCounter> getCollectionAggCodeCounterMap(List<CollectionAggCodeCounter> list, Map<String, String> aggMarkMap) {
        Map<String, CollectionAggCodeCounter> resMap = new HashMap<>();
        for(CollectionAggCodeCounter pojo : list) {
            //这里的key是上游有分组处理的逻辑key
            String key = getKey(pojo.getCollectionCode(), pojo.getAggCode(), pojo.getAggCodeType());
            pojo.setAggMark(aggMarkMap.get(pojo.getAggCode()));
            resMap.put(key, pojo);
        }
        return resMap;
    }

    private String getKey(String collectionCode, String aggCode, String aggCodeType) {
        return  collectionCode.concat(",").concat(aggCode).concat(",").concat(aggCodeType);
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.sumCollectedAggCodeByCollectionCodeInnerMark", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<CollectionAggCodeCounter> sumCollectedAggCodeByCollectionCodeInnerMark(
        List<CollectionCodeEntity> collectionCodeEntities, CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode,
        String collectedMark, Integer limit, Integer offset) {

        if (CollectionUtils.isEmpty(collectionCodeEntities) || null == aggCodeTypeEnum || StringUtils.isBlank(collectedMark)) {
            return Collections.emptyList();
        }
        List<String> collectionCodes = CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(collectionCodeEntities);
        if (CollectionUtils.isEmpty(collectionCodes)) {
            return Collections.emptyList();
        }

        List<CollectionRecordPo> collectionRecordPos = collectionRecordDao.findAggCodeByCollectedMark(collectionCodes, collectedMark, aggCodeTypeEnum, aggCode,
            1, null, 0, limit, offset);

        if (CollectionUtils.isEmpty(collectionRecordPos)) {
            return Collections.emptyList();
        }
        List<String> aggCodes = collectionRecordPos.parallelStream().map(CollectionRecordPo::getAggCode)
            .collect(Collectors.toList());
        Map<String, String> aggMarkMap = collectionRecordPos.stream().filter(collectionRecordPo -> StringUtils.isNotBlank(collectionRecordPo.getAggMark()))
            .collect(Collectors.toMap(CollectionRecordPo::getAggCode, CollectionRecordPo::getAggMark, (s, s2) -> s2));

        List<CollectionAggCodeCounter> res = this.sumAggCollectionByCollectionCode(collectionCodeEntities,aggCodes, aggCodeTypeEnum,collectedMark);

        return res.parallelStream()
            .filter(collectionAggCodeCounter -> collectionRecordPos.parallelStream().anyMatch(
                collectionRecordPo -> Objects.equals(collectionAggCodeCounter.getAggCode(), collectionRecordPo.getAggCode())
                    && Objects.equals(collectionAggCodeCounter.getCollectionCode(), collectionRecordPo.getCollectionCode())))
            .peek(collectionAggCodeCounter -> collectionAggCodeCounter.setAggMark(aggMarkMap.get(collectionAggCodeCounter.getAggCode())))
            .collect(Collectors.toList());

    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.sumCollectedAggCodeByCollectionCodeOutMark", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<CollectionAggCodeCounter> sumCollectedAggCodeByCollectionCodeOutMark(
        List<CollectionCodeEntity> collectionCodeEntities, CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode,
        String collectedMark, Integer limit, Integer offset) {

        if (CollectionUtils.isEmpty(collectionCodeEntities) || null == aggCodeTypeEnum || StringUtils.isBlank(collectedMark)) {
            return Collections.emptyList();
        }
        List<String> collectionCodes = CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(collectionCodeEntities);
        if (CollectionUtils.isEmpty(collectionCodes)) {
            return Collections.emptyList();
        }

        List<CollectionRecordPo> collectionRecordPos = collectionRecordDao.findAggCodeByCollectedMark(collectionCodes, collectedMark, aggCodeTypeEnum, aggCode,
            1, null, 1, limit, offset);

        if (CollectionUtils.isEmpty(collectionRecordPos)) {
            return Collections.emptyList();
        }
        List<String> aggCodes = collectionRecordPos.parallelStream().map(CollectionRecordPo::getAggCode)
            .collect(Collectors.toList());
        Map<String, String> aggMarkMap = collectionRecordPos.stream().filter(collectionRecordPo -> StringUtils.isNotBlank(collectionRecordPo.getAggMark()))
            .collect(Collectors.toMap(CollectionRecordPo::getAggCode, CollectionRecordPo::getAggMark, (s, s2) -> s2));

        List<CollectionAggCodeCounter> res = this.sumAggCollectionByCollectionCode(collectionCodeEntities,aggCodes, aggCodeTypeEnum,collectedMark);

        return res.parallelStream()
            .filter(collectionAggCodeCounter -> collectionRecordPos.parallelStream().anyMatch(
                collectionRecordPo -> Objects.equals(collectionAggCodeCounter.getAggCode(), collectionRecordPo.getAggCode())
                    && Objects.equals(collectionAggCodeCounter.getCollectionCode(), collectionRecordPo.getCollectionCode())))
            .peek(collectionAggCodeCounter -> collectionAggCodeCounter.setAggMark(aggMarkMap.get(collectionAggCodeCounter.getAggCode())))
            .collect(Collectors.toList());
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.sumAggCollectionByCollectionCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<CollectionAggCodeCounter> sumAggCollectionByCollectionCode(
        List<CollectionCodeEntity> collectionCodeEntities, List<String> aggCodes,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark) {
        if (CollectionUtils.isEmpty(collectionCodeEntities) || CollectionUtils.isEmpty(aggCodes) || null == aggCodeTypeEnum || StringUtils.isBlank(collectedMark)) {
            return Collections.emptyList();
        }
        List<String> collectionCodes = CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(collectionCodeEntities);
        if (CollectionUtils.isEmpty(collectionCodes)) {
            return Collections.emptyList();
        }

        Map<String, CollectionBusinessTypeEnum> aggMarkMap =
            collectionCodeEntities.stream().collect(Collectors.toMap(CollectionCodeEntity::getCollectionCode, CollectionCodeEntity::getBusinessType, (s, s2) -> s2));

        List<CollectionCollectedMarkCounter> collectionScanMarkCounters = collectionRecordDao.sumAggCollectionByCollectionCode(collectionCodes, aggCodes, aggCodeTypeEnum);

        collectionScanMarkCounters.forEach(collectionCollectedMarkCounter -> {
            collectionCollectedMarkCounter.setAggCodeType(aggCodeTypeEnum.name());
            collectionCollectedMarkCounter.setBusinessType(aggMarkMap.get(collectionCollectedMarkCounter.getCollectionCode()));
        });

        return CollectionEntityConverter.convertCollectionCollectedMarkCounterToCollectionAggCodeCounter(collectionScanMarkCounters, collectedMark);
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.queryCollectionScanDetailByAggCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<CollectionScanCodeDetail> queryCollectionScanDetailByAggCode(
        List<CollectionCodeEntity> collectionCodeEntities, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark, Integer limit, Integer offset) {

        if (CollectionUtils.isEmpty(collectionCodeEntities) || StringUtils.isBlank(aggCode) || null == aggCodeTypeEnum) {
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
                    StringUtils.isBlank(collectionRecordDetailPo.getCollectedMark())?
                        CollectionCollectedMarkTypeEnum.none :
                        Objects.equals(collectionRecordDetailPo.getCollectedMark(), collectedMark)?
                            CollectionCollectedMarkTypeEnum.inner : CollectionCollectedMarkTypeEnum.outer
                )
                .ts(collectionRecordDetailPo.getTs())
                .build()
            )
            .collect(Collectors.toList());

    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.getMaxTimeStampByCollectionCodesAndAggCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Timestamp getMaxTimeStampByCollectionCodesAndAggCode(List<CollectionCodeEntity> collectionCodeEntities,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode) {

        return collectionRecordDao.getMaxTimeStampByCollectionCodesAndAggCode(
            CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(collectionCodeEntities),
            aggCodeTypeEnum, aggCode
            );
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.getMaxTimeStampByCollectionCodesAndCollectedMark", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Timestamp getMaxTimeStampByCollectionCodesAndCollectedMark(List<CollectionCodeEntity> collectionCodeEntities,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark) {
        return collectionRecordDao.getMaxTimeStampByCollectionCodesAndCollectedMark(
            CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(collectionCodeEntities),
            aggCodeTypeEnum, collectedMark
        );
    }


    @Override
    public String getCollectionCode(CollectionCodeEntity codeEntity, String erp) {
        String code = this.getOrGenJQCodeByBusinessType(codeEntity,erp);
        if(StringUtils.isBlank(code)) {
            log.error("CollectionRecordServiceImpl.getCollectionCode:获取collectionCode为空,参数={}", JsonUtils.toJSONString(codeEntity));
            throw new JyBizException("获取collectionCode为空");
        }
        return code;
    }
}
