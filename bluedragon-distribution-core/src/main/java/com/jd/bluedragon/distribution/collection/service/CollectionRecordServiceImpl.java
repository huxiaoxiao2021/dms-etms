package com.jd.bluedragon.distribution.collection.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.collection.dao.CollectionRecordDao;
import com.jd.bluedragon.distribution.collection.entity.*;
import com.jd.bluedragon.distribution.collection.enums.CollectionStatusEnum;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;

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
                    collectionScanCodeEntity -> {
                        CollectionRecordDetailPo detailPo = new CollectionRecordDetailPo();
                        detailPo.setCollectionCode(collectionCode);
                        detailPo.setScanCode(collectionScanCodeEntity.getScanCode());
                        detailPo.setScanCodeType(collectionScanCodeEntity.getScanCodeType());
                        detailPo.setAggCode(collectionScanCodeEntity.getCollectionAggCodeMaps().getOrDefault(aggCodeTypeEnum, "null"));
                        detailPo.setAggCodeType(aggCodeTypeEnum.name());
                        detailPo.setCollectedStatus(CollectionStatusEnum.none_collected.getStatus());
                        return detailPo;
                    }).collect(Collectors.toList());
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
                        collectionScanCodeEntity -> {
                            CollectionRecordDetailPo detailPo = new CollectionRecordDetailPo();
                            detailPo.setCollectionCode(collectionCode);
                            detailPo.setScanCode(collectionScanCodeEntity.getScanCode());
                            detailPo.setScanCodeType(collectionScanCodeEntity.getScanCodeType());
                            detailPo.setAggCode(
                                collectionScanCodeEntity.getCollectionAggCodeMaps().getOrDefault(aggCodeTypeEnum, "null")
                            );
                            detailPo.setAggCodeType(aggCodeTypeEnum.name());
                            detailPo.setCollectedStatus(CollectionStatusEnum.none_collected.getStatus());
                            return detailPo;
                        }).collect(Collectors.groupingBy(CollectionRecordDetailPo::getAggCode));

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
                    CollectionRecordDetailPo collectionRecordDetailCondition = new CollectionRecordDetailPo();
                    collectionRecordDetailCondition.setCollectionCode(collectionCode);
                    collectionRecordDetailCondition.setAggCode(aggCode);
                    collectionRecordDetailCondition.setAggCodeType(aggCodeTypeEnum.name());
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
    public boolean collectTheScanCode(CollectionCollectorEntity collectionCollectorEntity, Result<Boolean> result) {
        if (null == collectionCollectorEntity) {
            result.toFail("收集的待集齐单号不存在");
            result.setData(false);
            return false;
        }
        List<CollectionCodeEntity> collectionCodeEntities = collectionCollectorEntity.genCollectionCodeEntity();
        if (CollectionUtils.isEmpty(collectionCodeEntities)) {
            result.toFail("未查询到当前的待集齐数据 ");
            result.setData(false);
            return false;
        }

        /* 根据condition去business_code_attribute表中查询所有的集合ID */
        //TODO 是否查询kv_index表



        return false;
    }
}
