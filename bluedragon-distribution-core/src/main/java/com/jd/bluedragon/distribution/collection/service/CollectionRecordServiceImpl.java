package com.jd.bluedragon.distribution.collection.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.busineCode.jqCode.JQCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.collection.builder.CollectionEntityConverter;
import com.jd.bluedragon.distribution.collection.dao.CollectionRecordDao;
import com.jd.bluedragon.distribution.collection.dao.CollectionRecordDetailDao;
import com.jd.bluedragon.distribution.collection.entity.*;
import com.jd.bluedragon.distribution.collection.enums.*;
import com.jd.bluedragon.distribution.collection.exception.CollectLockFailException;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BitMapUtil;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.fastjson.JSON;
import com.jd.jim.cli.Cluster;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
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


    /**
     * 自动过期时间 10分钟
     */
    private final static long REDIS_CACHE_EXPIRE_TIME = 10 * 60;


    private final static int SQL_IN_MAX_LENGTH = 300;
    private final static int JIQI_TASK_WAYBILL_PAGE = 100;
    private final static int WAYBILL_MAX_PACKAGE_SIZE = 50000;//最大5W个包裹(运单获取不到时默认bits是5W)

    @Autowired
    private CollectionRecordDao collectionRecordDao;
    @Autowired
    private CollectionRecordDetailDao collectionRecordDetailDao;
    @Autowired
    private JimDbLock jimDbLock;
    @Autowired
    private KvIndexDao kvIndexDao;
    @Autowired
    private JQCodeService jqCodeService;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClient;
    @Autowired
    private WaybillQueryManager waybillQueryManager;

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
                    if(!WaybillUtil.isWaybillCode(aggCode)) {
                        log.warn("initPartCollection集齐服务当前处理aggCode是运单的逻辑，非运单场景后续要扩展aggCode={}", aggCode);
                        throw new JyBizException("集齐服务当前仅处理aggCode是运单的场景");
                    }
                    int goodNumber = getWaybillGoodNumber(aggCode);
                    /* 对集齐明细数进行分页处理 */
                    initPartCollectionCollectRecordDetailHandler(collectionRecordDetailPos, collectionCode, aggCode, aggCodeTypeEnum, goodNumber);
                    /* 对集齐主表数据进行处理 */
                    initPartCollectionCollectRecordHandler(collectionCreatorEntity, collectionRecordDetailPos, collectionCode, aggCode, aggCodeTypeEnum);
                });
            });

        return true;
    }

    private int getWaybillGoodNumber(String waybillCode) {
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
        int goodNumber = WAYBILL_MAX_PACKAGE_SIZE;
        if (!Objects.isNull(waybill) && !Objects.isNull(waybill.getGoodNumber()) && waybill.getGoodNumber() > 0) {
            goodNumber = waybill.getGoodNumber();
        }
        return goodNumber;
    }

    /**
     * 集齐初始化处理明细表逻辑
     * 加锁
     */
    private void initPartCollectionCollectRecordDetailHandler(List<CollectionRecordDetailPo> collectionRecordDetailPos, String collectionCode, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum, int goodNumber) {
        collectRecordDetailHandlerLock(collectionRecordDetailPos, collectionCode, aggCode, goodNumber);
        try{
            Lists.partition(collectionRecordDetailPos, Constants.DEFAULT_PAGE_SIZE).forEach(partitionDetailPos -> {
                /* 检查当前这批数据下的明细表的数据是否已经存在，在增量模式下，不会对已有的数据造成影响 */
                    List<CollectionRecordDetailPo> collectionRecordDetailPosExist =
                            collectionRecordDetailDao.findExistDetails(collectionCode,
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
                Integer barthInsertNum = collectionRecordDetailDao.batchInsertCollectionRecordDetail(collectionRecordDetailPosNotExist);
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
            if (CollectionUtils.isNotEmpty(collectionRecordDetailPosExtraExist)) {
                Integer barthUpdateNum =collectionRecordDetailDao.updateDetailInfoByScanCodes(collectionCode,
                        collectionRecordDetailPosExtraExist.parallelStream().map(CollectionRecordDetailPo::getScanCode)
                                .collect(Collectors.toList()), aggCode, aggCodeTypeEnum, CollectionStatusEnum.collected,null);
                if(log.isInfoEnabled()) {
                    log.info("initPartCollection:批量修改明细数量barthInsertNum={},collectionCode={},aggCode={}", barthUpdateNum, collectionCode, aggCode);
                }
            }
            });
        }catch (Exception e) {
            log.error("initPartCollection:集齐初始化包裹处理部分异常collectionCode={},aggCode={},errMsg={}", collectionCode, aggCode, e.getMessage(), e);
            throw new JyBizException("集齐初始化包裹处理部分异常");
        }finally {
            collectionCollectRecordDetailHandlerUnLock(collectionRecordDetailPos, collectionCode, aggCode, goodNumber);
        }
    }

    private void collectRecordDetailHandlerLock(List<CollectionRecordDetailPo> collectionRecordDetailPos, String collectionCode, String aggCode, int goodNumber) {
        String aggCodeKey = MessageFormat.format(Constants.JQ_DETAIL_AGG_LOCK_PREFIX, collectionCode,aggCode);
        if (!jimDbLock.lock(aggCodeKey,"1", 10L, TimeUnit.MINUTES)) {
            log.warn("");
            throw new CollectLockFailException("集齐初始化包裹list分段锁资源竞争失败");
        }
        try{
            //内存中本次处理批包裹的二进制字符串
            String localBitsBinaryString = getLocalBatchPackageBitsBinaryString(collectionRecordDetailPos, goodNumber);
            String partPackageKey =  MessageFormat.format(Constants.JQ_DETAIL_AGG_BIT_LOCK_PREFIX, collectionCode,aggCode);
            //redis中维护正在处理中的bits(16进制)
            String redisValueString = redisClient.get(partPackageKey);
            if(StringUtils.isEmpty(redisValueString)) {
                String value = BitMapUtil.turn2to16(localBitsBinaryString);
                redisClient.setEx(partPackageKey, value, 10, TimeUnit.MINUTES);
            }else {
                String cacheString = BitMapUtil.turn16to2(redisValueString);
                boolean flag = packageLockConflictCheck(partPackageKey, localBitsBinaryString, cacheString);
                if(flag) {
                    log.warn("集齐操作包裹批锁存在部分包裹锁冲突：collectionCode={},aggCod{},goodNumber={}", collectionCode, aggCode, goodNumber);
                    throw new CollectLockFailException("集齐操作包裹批锁存在部分包裹锁冲突");
                }else {
                    String valueString = buildCacheBitsString(localBitsBinaryString, cacheString);
                    redisClient.setEx(partPackageKey, valueString, 10, TimeUnit.MINUTES);
                }
            }
        }catch (Exception e) {
            throw new CollectLockFailException("initPartCollectionCollectRecordDetailHandlerLock" + e.getMessage());
        }finally {
            jimDbLock.releaseLock(aggCodeKey,"1");
        }
    }

    /**
     * 合并两段bits
     * 理论上：两个二进制走按位或逻辑 |
     * @param localBitsBinaryString
     * @param localBitsBinaryString
     * @return
     */
    private String buildCacheBitsString(String localBitsBinaryString, String cacheBitsBinaryString) {
        if(StringUtils.isBlank(localBitsBinaryString) || StringUtils.isBlank(cacheBitsBinaryString)) {
            return localBitsBinaryString + cacheBitsBinaryString;
        }
        int length = Math.min(localBitsBinaryString.length(), cacheBitsBinaryString.length());
        String lStr;
        String cStr;
        String pre = "";
        if(length == localBitsBinaryString.length()) {
            lStr = localBitsBinaryString;
        }else {
            lStr  = localBitsBinaryString.substring(localBitsBinaryString.length() - length);
            pre = localBitsBinaryString.substring(0, localBitsBinaryString.length() - length);
        }
        if(length == cacheBitsBinaryString.length()) {
            cStr = cacheBitsBinaryString;
        }else {
            cStr  = cacheBitsBinaryString.substring(cacheBitsBinaryString.length() - length);
            pre = cacheBitsBinaryString.substring(0, cacheBitsBinaryString.length() - length);
        }
        StringBuffer sb = new StringBuffer();
        sb.append(pre);
        for(int i = 0; i < lStr.length(); i++) {
            sb.append((Objects.equals(lStr.charAt(i), '1')) ? '1' : cStr.charAt(i));
        }
        return BitMapUtil.turn2to16(sb.toString());
    }

    /**
     * 判断内存中的二进制传和redis缓存中二进制字符串中是否存在冲突位
     * 按位与 & > 0
     * @param localBitsBinaryString
     * @param cacheString
     * @return  true: 冲突   false: 无冲突
     */
    private boolean packageLockConflictCheck(String partPackageKey, String localBitsBinaryString, String cacheString) {
        if(StringUtils.isBlank(localBitsBinaryString) || StringUtils.isBlank(cacheString)) {
            return false;
        }
        int length = Math.min(localBitsBinaryString.length(),cacheString.length());
        String ls = localBitsBinaryString.substring(localBitsBinaryString.length() - length);
        String cs = cacheString.substring(cacheString.length() - length);

        for(int i = length - 1; i > 0; i--) {
            if(Objects.equals(ls.charAt(i), '1') && Objects.equals(cs.charAt(i), '1')) {
                if(log.isInfoEnabled()) {
                    log.info("cacheKey={}处理集齐包裹冲突，包裹下标是{}, cacheStr={},localStr={}", partPackageKey, i, cacheString, localBitsBinaryString);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前批的bits字符串
     * @param collectionRecordDetailPos
     * @param goodNumber
     * @return
     */
    private String getLocalBatchPackageBitsBinaryString(List<CollectionRecordDetailPo> collectionRecordDetailPos,int goodNumber) {
        byte[] bits = BitMapUtil.create(goodNumber);
        for(CollectionRecordDetailPo detailPo : collectionRecordDetailPos) {
            int currNum = WaybillUtil.getCurrentPackageNum(detailPo.getScanCode());
            BitMapUtil.add(bits, currNum);
        }
        return BitMapUtil.getBinaryString(bits);
    }

    private void collectionCollectRecordDetailHandlerUnLock(List<CollectionRecordDetailPo> collectionRecordDetailPos, String collectionCode, String aggCode, int goodNumber) {
        String aggCodeKey = MessageFormat.format(Constants.JQ_DETAIL_AGG_LOCK_PREFIX, collectionCode,aggCode);
        if (!jimDbLock.lock(aggCodeKey,"1", 10L, TimeUnit.MINUTES)) {
            log.warn("");
            return;
        }
        String localBitsBinaryString = "";
        try{
            localBitsBinaryString = getLocalBatchPackageBitsBinaryString(collectionRecordDetailPos, goodNumber);
            //内存中本次需要释放批包裹的二进制字符串
            String partPackageKey =  MessageFormat.format(Constants.JQ_DETAIL_AGG_BIT_LOCK_PREFIX, collectionCode,aggCode);
            //redis中维护正在处理中的bits(16进制)
            String redisValueString = redisClient.get(partPackageKey);
            if(StringUtils.isNotBlank(redisValueString)) {
                String cacheString = BitMapUtil.turn16to2(redisValueString);
                String valueString = releaseBatchPackageCacheBitsString(localBitsBinaryString, cacheString);
                redisClient.setEx(partPackageKey, valueString, 10, TimeUnit.MINUTES);
            }
        }catch (Exception e) {
            log.error("释放集齐aggCode锁失败，不对外抛异常，逻辑已经执行完毕，其他场景可能存在获取不到锁重试，等该锁过期后正常执行，aggCodeKey={},localBits={}", aggCodeKey, localBitsBinaryString);
        }finally {
            jimDbLock.releaseLock(aggCodeKey,"1");
        }
    }

    /**
     * redis缓存下线本地打标是1的
     * @param localBitsBinaryString
     * @param cacheBitsBinaryString
     * @return
     */
    private String releaseBatchPackageCacheBitsString(String localBitsBinaryString, String cacheBitsBinaryString) {
        int length = cacheBitsBinaryString.length();
        String releaseBitStr ;
        String cachePublicStr;
        StringBuffer sb = new StringBuffer();
        if(localBitsBinaryString.length() >= length) {
            releaseBitStr = localBitsBinaryString.substring(localBitsBinaryString.length() - length);
            cachePublicStr = cacheBitsBinaryString;
        }else {
            releaseBitStr = localBitsBinaryString;
            cachePublicStr = cacheBitsBinaryString.substring(length - localBitsBinaryString.length());
            sb.append(cacheBitsBinaryString.substring(0, length - localBitsBinaryString.length()));
        }
        for(int i = 0; i < releaseBitStr.length(); i++) {
            sb.append(Objects.equals(releaseBitStr.charAt(i),'1') ? '0' : cachePublicStr.charAt(i));
        }
        return BitMapUtil.turn2to16(sb.toString());
    }

    /**
     * 集齐主表数据修改
     */
    private void initPartCollectionCollectRecordHandler(CollectionCreatorEntity collectionCreatorEntity, List<CollectionRecordDetailPo> collectionRecordDetailPos, String collectionCode, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum) {
        String key = MessageFormat.format(Constants.JQ_AGG_LOCK_PREFIX, collectionCode,aggCodeTypeEnum.name(),aggCode);
        String collectedMark = collectionRecordDetailPos.parallelStream()
                .map(CollectionRecordDetailPo::getCollectedMark)
                .filter(StringUtils::isNotEmpty)
                .findAny().orElse("");
        if (jimDbLock.lock(key,"1", 10L, TimeUnit.MINUTES)) {
            try {
                //统计明细数据查询
                CollectionAggCodeCounter collectionAggCodeCounter = this.sumCollectionByAggCodeAndCollectionCode(
                        collectionCreatorEntity.getCollectionCodeEntity(), aggCode, aggCodeTypeEnum, collectedMark);
                if (log.isInfoEnabled()) {
                    log.info("initPartCollection:集齐服务统计数据结果集转换={},param=【{}|{}|{}|{}】", JsonUtils.toJSONString(collectionAggCodeCounter),
                            collectionCreatorEntity.getCollectionCodeEntity(), aggCode, aggCodeTypeEnum, collectedMark);
                }
                CollectionRecordPo collectionRecordPo = new CollectionRecordPo();
                collectionRecordPo.setCollectionCode(collectionCode);
                collectionRecordPo.setAggCode(aggCode);
                collectionRecordPo.setAggCodeType(aggCodeTypeEnum.name());
                collectionRecordPo.setInitNumber(collectionAggCodeCounter.getSumScanNum());
                collectionRecordPo.setIsInit(Constants.NUMBER_ONE);
                collectionRecordPo.setIsCollected(collectionAggCodeCounter.getCollectedNum() > 0 && collectionAggCodeCounter.getNoneCollectedNum() == 0 ?
                        Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                collectionRecordPo.setIsExtraCollected(collectionAggCodeCounter.getExtraCollectedNum() > 0 ?
                        Constants.NUMBER_ONE : Constants.NUMBER_ZERO);

                if (StringUtils.isNotEmpty(collectedMark)) {
                    collectionRecordPo.setIsMoreCollectedMark(collectionAggCodeCounter.getOutMarkCollectedNum() > 0 ?
                            Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                }
                collectionRecordPo.setAggMark(collectionCreatorEntity.getCollectionAggMarks().getOrDefault(aggCode, ""));
                //插入删除
                if (log.isInfoEnabled()) {
                    log.info("initPartCollection：集齐主表统计插入修改collectionRecordPo={}", JsonUtils.toJSONString(collectionRecordPo));
                }
                if (collectionRecordDao.updateCollectionRecord(collectionRecordPo) <= 0) {
                    collectionRecordPo.setIsMoreCollectedMark(Constants.NUMBER_ZERO);
                    Integer insertNum = collectionRecordDao.insertCollectionRecord(collectionRecordPo);
                    if (insertNum <= 0 && log.isInfoEnabled()) {
                        log.info("initPartCollection：集齐主表统计插入数量为0，collectionRecordPo={}", JsonUtils.toJSONString(collectionRecordPo));
                    }
                }

            }catch (Exception e) {
                log.error("集齐初始化主表统计数据处理异常collectionCreatorEntity={}，collectionCode={},aggCode={},errMsg={}",
                        JsonUtils.toJSONString(collectionCreatorEntity), collectionCode, aggCode, e.getMessage(), e);
                throw new JyBizException("集齐初始化主表统计数据处理异常" + collectionCode + aggCode);
            }finally {
                jimDbLock.releaseLock(key, "1");
            }
        }else {
            //获取不到锁抛异常外部jmq-consumer走重试
            log.warn("集齐初始化主表统计数据处理没有获取到锁，异常重试，collectionCreatorEntity={}，collectionCode={},aggCode={}",
                    JsonUtils.toJSONString(collectionCreatorEntity), collectionCode, aggCode);
            throw new CollectLockFailException("集齐初始化主表统计数据处理没有获取到锁，异常重试" + collectionCode + aggCode);
        }
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
                    if(!WaybillUtil.isWaybillCode(aggCode)) {
                        log.warn("集齐服务当前处理aggCode是运单的逻辑，非运单场景后续要扩展aggCode={}", aggCode);
                        throw new JyBizException("集齐服务当前仅处理aggCode是运单的场景");
                    }
                    int goodNumber = getWaybillGoodNumber(aggCode);
                    initAndCollectedPartCollectionCollectRecordDetailHandler(collectionRecordDetailPos, collectionCode, aggCode, aggCodeTypeEnum, goodNumber);
                    initAndCollectedPartCollectionCollectRecordHandler(collectionRecordDetailPos, collectionCode, aggCode, aggCodeTypeEnum, collectionCreatorEntity);
                });

            });

        return true;
    }

    /**
     * 集齐初始化并修改集齐状态明细处理
     */
    private void initAndCollectedPartCollectionCollectRecordDetailHandler(List<CollectionRecordDetailPo> collectionRecordDetailPos,
                                                                          String collectionCode,
                                                                          String aggCode,
                                                                          CollectionAggCodeTypeEnum aggCodeTypeEnum, int goodNumber) {
        collectRecordDetailHandlerLock(collectionRecordDetailPos, collectionCode, aggCode, goodNumber);
        try {
            /* 对包裹列表数进行分页处理 */
            Lists.partition(collectionRecordDetailPos, Constants.DEFAULT_PAGE_SIZE).forEach(partitionDetailPos -> {
                /* 检查当前这批数据下的明细表的数据是否已经存在，在增量模式下，不会对已有的数据造成影响 */
                List<CollectionRecordDetailPo> collectionRecordDetailPosExist =
                        collectionRecordDetailDao.findExistDetails(collectionCode,
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
                    Integer batchInsertNum = collectionRecordDetailDao.batchInsertCollectionRecordDetail(collectionRecordDetailPosNotExist);
                    if (log.isInfoEnabled()) {
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
                        .forEach((collectedMark, itemCollectionRecordDetailPosExtraExist) -> {
                            /* 更新到数据库中 */
                            Integer batchUpdateNum = collectionRecordDetailDao.updateDetailInfoByScanCodes(collectionCode,
                                    itemCollectionRecordDetailPosExtraExist.parallelStream().map(CollectionRecordDetailPo::getScanCode)
                                            .collect(Collectors.toList()), aggCode, aggCodeTypeEnum, CollectionStatusEnum.collected, collectedMark);
                            if (log.isInfoEnabled()) {
                                log.info("initAndCollectedPartCollection:批量修改数量为{}，collectionCode-{},aggCode={}", batchUpdateNum, collectionCode, aggCode);
                            }
                        });
            });
        } catch (Exception e) {
            log.error("initAndCollectedPartCollection:集齐初始化并修改集齐状态包裹处理部分异常collectionCode={},aggCode={},errMsg={}", collectionCode, aggCode, e.getMessage(), e);
            throw new JyBizException("集齐初始化并修改集齐状态包裹处理部分异常");
        }finally {
            collectionCollectRecordDetailHandlerUnLock(collectionRecordDetailPos, collectionCode, aggCode, goodNumber);
        }
    }

    /**
     * 集齐初始化并修改集齐状态主表统计处理
     */
    private void initAndCollectedPartCollectionCollectRecordHandler(List<CollectionRecordDetailPo> collectionRecordDetailPos,
                                                                    String collectionCode,
                                                                    String aggCode,
                                                                    CollectionAggCodeTypeEnum aggCodeTypeEnum,
                                                                    CollectionCreatorEntity collectionCreatorEntity) {
        String key = MessageFormat.format(Constants.JQ_AGG_LOCK_PREFIX, collectionCode,aggCodeTypeEnum.name(),aggCode);
        if (jimDbLock.lock(key,"1", 10L, TimeUnit.MINUTES)) {
            try {

                String collectedMark = collectionRecordDetailPos.parallelStream()
                        .map(CollectionRecordDetailPo::getCollectedMark)
                        .filter(StringUtils::isNotEmpty)
                        .findAny().orElse("");
                CollectionAggCodeCounter collectionAggCodeCounter = this.sumCollectionByAggCodeAndCollectionCode(
                        collectionCreatorEntity.getCollectionCodeEntity(),
                        aggCode, aggCodeTypeEnum, collectedMark

                );
                if (log.isInfoEnabled()) {
                    log.info("initAndCollectedPartCollection:集齐服务统计数据结果集转换={},param=【{}|{}|{}|{}】", JsonUtils.toJSONString(collectionAggCodeCounter),
                            collectionCreatorEntity.getCollectionCodeEntity(), aggCode, aggCodeTypeEnum, collectedMark);
                }
                CollectionRecordPo collectionRecordPo = new CollectionRecordPo();
                collectionRecordPo.setCollectionCode(collectionCode);
                collectionRecordPo.setAggCode(aggCode);
                collectionRecordPo.setAggCodeType(aggCodeTypeEnum.name());
                collectionRecordPo.setInitNumber(collectionAggCodeCounter.getSumScanNum());
                collectionRecordPo.setIsInit(Constants.NUMBER_ONE);
                collectionRecordPo.setIsCollected(collectionAggCodeCounter.getCollectedNum() > 0 && collectionAggCodeCounter.getNoneCollectedNum() == 0 ?
                        Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                collectionRecordPo.setIsExtraCollected(collectionAggCodeCounter.getExtraCollectedNum() > 0 ?
                        Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                collectionRecordPo.setIsMoreCollectedMark(collectionAggCodeCounter.getOutMarkCollectedNum() > 0 ?
                        Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                collectionRecordPo.setAggMark(collectionCreatorEntity.getCollectionAggMarks().getOrDefault(aggCode, ""));

                if (log.isInfoEnabled()) {
                    log.info("initAndCollectedPartCollection:插入或修改集齐主表数据collectionRecordPo={}", JsonUtils.toJSONString(collectionRecordPo));
                }
                if (collectionRecordDao.updateCollectionRecord(collectionRecordPo) <= 0) {
                    Integer insertNum = collectionRecordDao.insertCollectionRecord(collectionRecordPo);
                    if (log.isInfoEnabled()) {
                        log.info("initAndCollectedPartCollection:插入集齐主表数据0条，collectionRecordPo={}", JsonUtils.toJSONString(collectionRecordPo));
                    }
                }
                jimDbLock.releaseLock(key, "1");
            }catch (Exception e) {
                log.error("集齐初始化并修改集齐状态主表统计数据处理异常collectionCreatorEntity={}，collectionCode={},aggCode={},errMsg={}",
                        JsonUtils.toJSONString(collectionCreatorEntity), collectionCode, aggCode, e.getMessage(), e);
                throw new JyBizException("集齐初始化并修改集齐状态主表统计数据处理异常" + collectionCode + aggCode);
            }finally {
                jimDbLock.releaseLock(key, "1");
            }
        }else {
            //获取不到锁抛异常外部jmq-consumer走重试
            log.warn("集齐初始化并修改集齐状态主表统计数据处理没有获取到锁，异常重试，collectionCreatorEntity={}，collectionCode={},aggCode={}",
                    JsonUtils.toJSONString(collectionCreatorEntity), collectionCode, aggCode);
            throw new CollectLockFailException("集齐初始化并修改集齐状态主表统计数据处理没有获取到锁，异常重试" + collectionCode + aggCode);
        }
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
            //明细表处理
            collectTheScanCodeCollectRecordDetailHandler(collectionCodeEntity, scanCode, scanCodeType, collectedMark, finalElement);
            //主表处理
            collectTheScanCodeCollectRecordHandler(collectionCollectorEntity, collectionCodeEntity, scanCode, collectedMark);
        });

        return true;
    }
    /**
     * 单条集齐状态变更修改主表数据
     */
    private void collectTheScanCodeCollectRecordDetailHandler(CollectionCodeEntity collectionCodeEntity, String scanCode, String scanCodeType, String collectedMark, Map<CollectionAggCodeTypeEnum, String> finalElement) {
        List<CollectionRecordDetailPo> collectionRecordDetailPos1 = new ArrayList<>();
        collectionRecordDetailPos1.add(CollectionRecordDetailPo.builder().scanCode(scanCode).build());
        String collectionCode = collectionCodeEntity.getCollectionCode();
        CollectionAggCodeTypeEnum aggCodeTypeEnum1 = collectionCodeEntity.getBusinessType().getCollectionAggCodeTypes().get(0);
        String aggCode = finalElement.getOrDefault(aggCodeTypeEnum1, "null");
        if(!WaybillUtil.isWaybillCode(aggCode)) {
            log.warn("集齐服务当前处理aggCode是运单的逻辑，非运单场景后续要扩展aggCode={}", aggCode);
            throw new JyBizException("集齐服务当前仅处理aggCode是运单的场景");
        }
        int goodNumber = getWaybillGoodNumber(aggCode);
        collectRecordDetailHandlerLock(collectionRecordDetailPos1, collectionCode, aggCode, goodNumber);
        try {
            /* 检查该待集齐集合中是否有这单，检查是否是待集齐的状态 */
            List<CollectionRecordDetailPo> scanDetailPos = collectionRecordDetailDao.findCollectionRecordDetail(CollectionRecordDetailPo.builder()
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

                Integer batchInsertNum = collectionRecordDetailDao.batchInsertCollectionRecordDetail(collectionRecordDetailPos);
                if (log.isInfoEnabled()) {
                    log.info("collectTheScanCode:批量插入数量为{},collectionRecordDetailPos(此场景理论只有一条数据)={}", batchInsertNum, JsonUtils.toJSONString(collectionRecordDetailPos));
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
                Integer batchUpdateNum = collectionRecordDetailDao.updateCollectionRecordDetail(collectionRecordDetailPo);
                if (log.isInfoEnabled()) {
                    log.info("collectTheScanCode:批量插入数量为{}, collectionRecordDetailPo={}", batchUpdateNum, JsonUtils.toJSONString(collectionRecordDetailPo));
                }
            }
        }catch (Exception e) {
            log.error("collectTheScanCode:集齐单条修改集齐状态包裹处理部分异常collectionCodeEntity={},scanCode={},collectedMark={},finalElement={}，errMsg={}",
                    JsonUtils.toJSONString(collectionCodeEntity), scanCode, collectedMark, JsonUtils.toJSONString(finalElement), e.getMessage(), e);
            throw new JyBizException("集齐初始化并修改集齐状态包裹处理部分异常");
        }finally {
            collectionCollectRecordDetailHandlerUnLock(collectionRecordDetailPos1, collectionCode, aggCode, goodNumber);
        }
    }

    /**
     * 单条集齐状态变更修改主表数据
     */
    private void collectTheScanCodeCollectRecordHandler(CollectionCollectorEntity collectionCollectorEntity,
                                                        CollectionCodeEntity collectionCodeEntity,
                                                        String scanCode,
                                                        String collectedMark) {
        /* 更新主表 */
        List<CollectionRecordDetailPo> aggCodeDetailPos = collectionRecordDetailDao.findAggCodeByScanCode(
                CollectionRecordDetailPo.builder()
                        .collectionCode(collectionCodeEntity.getCollectionCode())
                        .scanCode(scanCode)
                        .build()
        );

        aggCodeDetailPos.forEach(aggCodeDetailPo -> {
            String key = MessageFormat.format(Constants.JQ_AGG_LOCK_PREFIX, collectionCodeEntity.getCollectionCode(),aggCodeDetailPo.getAggCodeType(),aggCodeDetailPo.getAggCode());
            if (jimDbLock.lock(key,"1", 10L, TimeUnit.MINUTES)) {
                try {
                    /* 根据collectionCode， aggCodeType， aggCode， 查询是否集齐 */
                    CollectionAggCodeCounter collectionAggCodeCounter = this.sumCollectionByAggCodeAndCollectionCode(
                            collectionCodeEntity, aggCodeDetailPo.getAggCode(),
                            CollectionAggCodeTypeEnum.valueOf(aggCodeDetailPo.getAggCodeType()), collectedMark
                    );
                    if (log.isInfoEnabled()) {
                        log.info("collectTheScanCode:集齐服务统计数据结果集转换={},param=【{}|{}|{}|{}】", JsonUtils.toJSONString(collectionAggCodeCounter),
                                collectionCodeEntity, aggCodeDetailPo.getAggCode(), CollectionAggCodeTypeEnum.valueOf(aggCodeDetailPo.getAggCodeType()), collectedMark);
                    }
                    CollectionRecordPo collectionRecordPo = new CollectionRecordPo();
                    collectionRecordPo.setCollectionCode(collectionCodeEntity.getCollectionCode());
                    collectionRecordPo.setAggCode(aggCodeDetailPo.getAggCode());
                    collectionRecordPo.setAggCodeType(aggCodeDetailPo.getAggCodeType());
                    collectionRecordPo.setInitNumber(collectionAggCodeCounter.getSumScanNum());
                    collectionRecordPo.setIsCollected(collectionAggCodeCounter.getCollectedNum() > 0 && collectionAggCodeCounter.getNoneCollectedNum() == 0 ?
                            Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                    collectionRecordPo.setIsExtraCollected(collectionAggCodeCounter.getExtraCollectedNum() > 0 ?
                            Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                    if (StringUtils.isNotBlank(collectionCollectorEntity.getAggMark())) {
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
                        if (insertNum <= 0 && log.isInfoEnabled()) {
                            log.info("collectTheScanCode:主表插入数量为0，collectionRecordPo{}", JsonUtils.toJSONString(collectionRecordPo));
                        }
                    }
                    jimDbLock.releaseLock(key, "1");

                }catch (Exception e) {
                    log.error("集齐修改单条状态主表统计数据处理异常collectionCollectorEntity={}，collectionCodeEntity={}, scanCode={}, collectedMark={}, errMsg={}",
                            JsonUtils.toJSONString(collectionCollectorEntity), JsonUtils.toJSONString(collectionCodeEntity), scanCode, collectedMark, e.getMessage(), e);
                    throw new JyBizException("集齐修改单条状态主表统计数据处理异常" + collectedMark + scanCode);
                }finally {
                    jimDbLock.releaseLock(key, "1");
                }
            }else {
                //获取不到锁抛异常外部jmq-consumer走重试
                log.warn("集齐修改单条状态主表统计数据处理没有获取到锁，异常重试，collectionCollectorEntity={}，collectionCodeEntity={}, scanCode={}, collectedMark={}",
                        JsonUtils.toJSONString(collectionCollectorEntity), JsonUtils.toJSONString(collectionCodeEntity), scanCode, collectedMark);
                throw new CollectLockFailException("集齐修改单条状态主表统计数据处理没有获取到锁，异常重试" + collectedMark + scanCode);
            }
        });
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
        List<CollectionCollectedMarkCounter> collectionCollectedMarkCounters = collectionRecordDetailDao.countCollectionByAggCodeAndCollectionCodes(collectionCodes, aggCode, aggCodeTypeEnum);
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

        List<CollectionCollectedMarkCounter> collectionCollectedMarkCounters = collectionRecordDetailDao
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
        if(log.isInfoEnabled()) {
            log.info("countNoneCollectedAggCodeNumByCollectionCode:查询集齐统计：collectionCodes={}，aggCodeTypeEnum={}， collectedMark={}",
                    JsonUtils.toJSONString(collectionCodes), JsonUtils.toJSONString(aggCodeTypeEnum), collectedMark);
        }
        return this.countAggCodeByCollectionCodesAndStatus(collectionCodes, aggCodeTypeEnum,
         collectedMark, Constants.NUMBER_ZERO, null);

    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.countCollectionAggCodeNumByCollectionCodeInnerMark", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Integer countCollectionAggCodeNumByCollectionCodeInnerMark(List<CollectionCodeEntity> collectionCodeEntities,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode, String collectedMark) {

        List<String> collectionCodes = CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(collectionCodeEntities);

        return this.countAggCodeByCollectionCodesAndStatus(collectionCodes, aggCodeTypeEnum,
            collectedMark, Constants.NUMBER_ONE, Constants.NUMBER_ZERO);

    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.countCollectionAggCodeNumByCollectionCodeOutMark", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Integer countCollectionAggCodeNumByCollectionCodeOutMark(List<CollectionCodeEntity> collectionCodeEntities,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode, String collectedMark) {

        List<String> collectionCodes = CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(collectionCodeEntities);

        return this.countAggCodeByCollectionCodesAndStatus(collectionCodes, aggCodeTypeEnum,
            collectedMark, Constants.NUMBER_ONE, Constants.NUMBER_ONE);
    }

    private Integer countAggCodeByCollectionCodesAndStatus(List<String> collectionCodes, CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark, Integer numberOne, Integer numberOne1) {
        List<String> aggCodes = collectionRecordDetailDao.getAggCodesByCollectedMark(collectionCodes, collectedMark,aggCodeTypeEnum.name());
        if(CollectionUtils.isEmpty(aggCodes)) {
            if(log.isInfoEnabled()) {
                log.info("countAggCodeByCollectionCodesAndStatus:根据collectionCode【{}】任务【{}】查明细aggCode数量为0", JsonUtils.toJSONString(collectionCodes), collectedMark);
            }
            return 0;
        }
        if(aggCodes.size() <= Constants.DB_IN_MAX_SIZE) {
            return collectionRecordDao.countAggCodeByCollectionCodesAndStatus(collectionCodes, aggCodes, aggCodeTypeEnum.name(), numberOne, numberOne1);
        }else {
            if(log.isInfoEnabled()) {
                log.info("countAggCodeByCollectionCodesAndStatus:根据collectionCode【{}】任务【{}】查aggCode数量为{}", JsonUtils.toJSONString(collectionCodes), collectedMark, aggCodes.size());
            }
            List<List<String>> aggCodesList = Lists.partition(aggCodes, Constants.DB_IN_MAX_SIZE);
            Integer res = 0;
            for(List<String> aggCodeList : aggCodesList) {
                res += collectionRecordDao.countAggCodeByCollectionCodesAndStatus(collectionCodes, aggCodeList, aggCodeTypeEnum.name(), numberOne, numberOne1);
            }
            return res;
        }
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

        List<CollectionRecordPo> collectionRecordPos = this.findAggCodeByCollectedMark(collectionCodes, collectedMark, aggCodeTypeEnum, aggCode,
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

        List<CollectionRecordPo> collectionRecordPos = this.findAggCodeByCollectedMark(collectionCodes, collectedMark, aggCodeTypeEnum, aggCode,
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

        List<CollectionRecordPo> collectionRecordPos = this.findAggCodeByCollectedMark(collectionCodes, collectedMark, aggCodeTypeEnum, aggCode,
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

    private List<CollectionRecordPo> findAggCodeByCollectedMark(List<String> collectionCodes,
                                                                String collectedMark,
                                                                CollectionAggCodeTypeEnum aggCodeTypeEnum,
                                                                String aggCode,
                                                                Integer isCollected,
                                                                Integer isExtraCollected,
                                                                Integer isMoreCollectedMark,
                                                                Integer limit,
                                                                Integer offset) {

//        return collectionRecordDao.findAggCodeByCollectedMark(collectionCodes, collectedMark, aggCodeTypeEnum, aggCode, isCollected, isExtraCollected, isMoreCollectedMark, limit, offset);
        if(StringUtils.isNotBlank(aggCode)) {
            return findAggCodeByCollectedMarkWithAggCode(collectionCodes,collectedMark,aggCodeTypeEnum,aggCode,isCollected,isExtraCollected,isMoreCollectedMark,limit,offset);
        }else {
            return findAggCodeByCollectedMarkWithoutAggCode(collectionCodes,collectedMark,aggCodeTypeEnum,aggCode,isCollected,isExtraCollected,isMoreCollectedMark,limit,offset);
        }
    }

    private List<CollectionRecordPo> findAggCodeByCollectedMarkWithAggCode(List<String> collectionCodes,
                                                                String collectedMark,
                                                                CollectionAggCodeTypeEnum aggCodeTypeEnum,
                                                                String aggCode,
                                                                Integer isCollected,
                                                                Integer isExtraCollected,
                                                                Integer isMoreCollectedMark,
                                                                Integer limit,
                                                                Integer offset) {
        List<CollectionRecordDetailPo> detailList = collectionRecordDetailDao.findByAggCode(collectionCodes, collectedMark,
                aggCodeTypeEnum, aggCode, isCollected, isExtraCollected, isMoreCollectedMark);
        if(CollectionUtils.isEmpty(detailList)) {
            return null;
        }
        if(detailList.size() > 2) {
            log.warn("当前场景按单{}搜索最多存在两个集合中，存在两条不同collectionCode的aggCode, 当前查出来大于2条[{}条]:collectionCodes={},collectedMark={}",
                    aggCode, detailList.size(), JsonUtils.toJSONString(collectionCodes), collectedMark);
        }
        List<CollectionRecordPo> resList = new ArrayList<>();
        //todo 目前模型detailList理论最多两条，风险保护，出现异常数据时只查前十条
        Lists.partition(detailList, 10).get(0).forEach(detailPo -> {
            resList.add(collectionRecordDao.findByAggCode(detailPo.getCollectionCode(), detailPo.getAggCodeType(),
                    detailPo.getAggCode(), isCollected, isExtraCollected, isMoreCollectedMark));
        });
        //按运单查无需排序，同一单流向肯定相同，出现不同肯定是DB中存储逻辑存在问题
        return resList;
    }


    private List<CollectionRecordPo> findAggCodeByCollectedMarkWithoutAggCode(List<String> collectionCodes, String collectedMark, CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode, Integer isCollected, Integer isExtraCollected, Integer isMoreCollectedMark, Integer limit, Integer offset) {
//        List<CollectionRecordPo> res = new ArrayList<>();
        //todo 这里分页处理主要考虑查明细返回运单，去主表过滤后不足100条，此时需要在查明细表组装，，限制最多查 cycleCount 次，避免一直查库
        int offsetTemp = 0;
        int limitTemp = 1000;
        int cycleCount = 2;
        List<CollectionRecordPo> res = new ArrayList<>();
        while(res.size() < JIQI_TASK_WAYBILL_PAGE && cycleCount > 0) {
            List<CollectionRecordDetailPo> detailPoListTemp = findDetailAggCodeByCollectedMark(collectionCodes, collectedMark, aggCodeTypeEnum, offsetTemp, limitTemp);
            if(CollectionUtils.isEmpty(detailPoListTemp)) {
                if(log.isInfoEnabled()) {
                    log.info("findAggCodeByCollectedMark根据任务查找任务下运单为空collectionCodes={},collectedMark={}", JsonUtils.toJSONString(collectionCodes), JsonUtils.toJSONString(collectedMark));
                }
                return null;
            }
            Map<String, List<CollectionRecordDetailPo>> map = detailPoListTemp.stream().collect(Collectors.groupingBy(CollectionRecordDetailPo::getCollectionCode));
            if(log.isInfoEnabled()) {
                log.info("findAggCodeByCollectedMark根据任务查找任务下运单，collectionList={},查明细返回aggCode数量={},offsetTemp={}, limitTemp",
                        JsonUtils.toJSONString(map.keySet()), detailPoListTemp.size(), offsetTemp, limitTemp);
            }
            //理论上map最大两个key
            map.forEach((collectionCode, collectionRecordDetailPoList) -> {
                List<String> aggCodeList =  collectionRecordDetailPoList.stream().map(CollectionRecordDetailPo::getAggCode).filter(aggCodeTemp -> StringUtils.isNotBlank(aggCodeTemp)).collect(Collectors.toList());
                //避免不同key中aggCode分布不均导致sql中aggCode in数量过大,对每个可能collectionCode进行拆分
                List<List<String>> aggCodeListList = Lists.partition(aggCodeList, SQL_IN_MAX_LENGTH);
                for(List<String> aggCodeList1 : aggCodeListList) {
                    List<CollectionRecordPo> collectionRecordPos1 = collectionRecordDao.findAggCodes(collectionCode, aggCodeTypeEnum.name(), aggCodeList1, isCollected, isExtraCollected, isMoreCollectedMark);
                    if(CollectionUtils.isEmpty(collectionRecordPos1)) {
                        continue;
                    }
                    int shortNum = JIQI_TASK_WAYBILL_PAGE - res.size();
                    if(collectionRecordPos1.size() < shortNum) {
                        res.addAll(collectionRecordPos1);
                        continue;
                    }else {
                        res.addAll(collectionRecordPos1.subList(0, shortNum));
                        break;
                    }
                }
            });
            offsetTemp += limitTemp;
            cycleCount--;
        }
        if(CollectionUtils.isEmpty(res)) {
            if(log.isInfoEnabled()) {
                log.info("findAggCodeByCollectedMark根据任务查找任务下运单查询结果为空collectionCodes={},collectedMark={}", JsonUtils.toJSONString(collectionCodes), JsonUtils.toJSONString(collectedMark));
            }
        }
        Collections.sort(res, new Comparator<CollectionRecordPo>() {
            @Override
            public int compare(CollectionRecordPo o1, CollectionRecordPo o2) {
                return o1.getAggMark().compareTo(o2.getAggMark());
            }
        });
        return res;
    }

    private List<CollectionRecordDetailPo> findDetailAggCodeByCollectedMark(List<String> collectionCodes, String collectedMark, CollectionAggCodeTypeEnum aggCodeTypeEnum, Integer limit, Integer offset) {
        return collectionRecordDetailDao.findAggCodeByCollectedMark(collectionCodes, collectedMark, aggCodeTypeEnum, limit, offset);
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

        List<CollectionCollectedMarkCounter> collectionScanMarkCounters = collectionRecordDetailDao.sumAggCollectionByCollectionCode(collectionCodes, aggCodes, aggCodeTypeEnum);

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

        List<CollectionRecordDetailPo> collectionRecordDetailPos = collectionRecordDetailDao.queryCollectedDetailByCollectionAndAggCode(
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

        return collectionRecordDetailDao.getMaxTimeStampByCollectionCodesAndAggCode(
            CollectionEntityConverter.getCollectionCodesFromCollectionCodeEntity(collectionCodeEntities),
            aggCodeTypeEnum, aggCode
            );
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.CollectionRecordService.getMaxTimeStampByCollectionCodesAndCollectedMark", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Timestamp getMaxTimeStampByCollectionCodesAndCollectedMark(List<CollectionCodeEntity> collectionCodeEntities,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark) {
        return collectionRecordDetailDao.getMaxTimeStampByCollectionCodesAndCollectedMark(
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
