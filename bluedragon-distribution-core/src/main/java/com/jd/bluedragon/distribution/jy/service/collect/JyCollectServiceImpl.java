package com.jd.bluedragon.distribution.jy.service.collect;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collection.entity.*;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionScanCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.service.CollectionRecordService;
import com.jd.bluedragon.distribution.jy.dto.collect.*;
import com.jd.bluedragon.distribution.jy.dto.unload.CollectStatisticsQueryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanCollectStatisticsDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.constant.CollectServiceConstant;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectSiteTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectSiteTypeServiceFactory;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectStatisticsDimensionFactory;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectSiteTypeService;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectStatisticsDimensionService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.erp.util.BeanUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author zhengchengfa
 * @Description //集齐服务实现
 * @date
 **/
@Service
public class JyCollectServiceImpl implements JyCollectService{
    private Logger log = LoggerFactory.getLogger(JyCollectServiceImpl.class);


    @Autowired
    private CollectionRecordService collectionRecordService;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private WaybillService waybillService;
    @Autowired
    private JyCollectCacheService jyCollectCacheService;
    @Autowired
    @Qualifier(value = "jyCollectBatchUpdateProducer")
    private DefaultJMQProducer jyCollectBatchUpdateProducer;
    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.findCollectInfo",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<CollectReportResDto> findCollectInfo(CollectReportReqDto collectReportReqDto) {
        InvokeResult<CollectReportResDto> res = new InvokeResult<>();
        CollectReportResDto resData = new CollectReportResDto();
        //空任务只有在库集齐
        if(collectReportReqDto.getManualCreateTaskFlag() != null && collectReportReqDto.getManualCreateTaskFlag()
                && CollectTypeEnum.SITE_JIQI.getCode() != collectReportReqDto.getCollectType()) {
            resData.setCollectReportStatisticsDtoList(null);
            resData.setCollectReportDtoList(null);
            resData.setCollectDimension(null);
            resData.setCollectType(null);
            resData.setManualCreateTaskFlag(true);
            return res;
        }
        resData.setCollectType(collectReportReqDto.getCollectType());
        resData.setManualCreateTaskFlag(false);
        res.setData(resData);
        //集齐统计数据
        resData.setCollectReportStatisticsDtoList(getCollectReportDetailPackageDtoList(collectReportReqDto));
        //集齐运单列表
        CollectStatisticsDimensionService collectStatisticsService = CollectStatisticsDimensionFactory.getCollectStatisticsDimensionService(collectReportReqDto.getCollectType());
        resData.setCollectReportDtoList(collectStatisticsService.queryCollectListPage(collectReportReqDto));
        return res;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.findCollectDetail",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<CollectReportDetailResDto> findCollectDetail(CollectReportReqDto collectReportReqDto) {
        InvokeResult<CollectReportDetailResDto> res = new InvokeResult<>();
        CollectReportDetailResDto resData = new CollectReportDetailResDto();
        res.setData(resData);
        //空任务只有在库集齐
        if(collectReportReqDto.getManualCreateTaskFlag() != null && collectReportReqDto.getManualCreateTaskFlag()
                && CollectTypeEnum.SITE_JIQI.getCode() != collectReportReqDto.getCollectType()) {
            resData.setCollectReportStatisticsDtoList(null);
            resData.setCollectReportDto(null);
            resData.setCollectReportDetailPackageDtoList(null);
            resData.setCollectDimension(null);
            resData.setCollectType(null);
            resData.setManualCreateTaskFlag(true);
            return res;
        }
        resData.setCollectType(collectReportReqDto.getCollectType());
        resData.setManualCreateTaskFlag(false);
        //集齐类型运单统计
        resData.setCollectReportStatisticsDtoList(getCollectReportDetailPackageDtoList(collectReportReqDto));

        CollectStatisticsDimensionService collectStatisticsService = CollectStatisticsDimensionFactory.getCollectStatisticsDimensionService(collectReportReqDto.getCollectType());
        //当前运单统计
        List<CollectReportDto> collectReportDtoList = collectStatisticsService.queryCollectListPage(collectReportReqDto);
        if(CollectionUtils.isEmpty(collectReportDtoList)) {
            res.setMessage("未查到当前集齐类型的运单数据");
            return res;
        }
        resData.setCollectReportDto(collectReportDtoList.get(0));
        //明细分页查询
        resData.setCollectReportDetailPackageDtoList(collectStatisticsService.queryCollectDetail(collectReportReqDto));
        return res;
    }

    /**
     * 聚合类型集齐统计数据
     * @param collectReportReqDto
     * @return
     */
    private List<CollectReportStatisticsDto> getCollectReportDetailPackageDtoList(CollectReportReqDto collectReportReqDto) {
        List<CollectReportStatisticsDto> res = new ArrayList<>();

        if (null == collectReportReqDto || null == collectReportReqDto.getCurrentOperate()) {
            return Collections.emptyList();
        }

        //查询待集齐集合ID 列表
        List<CollectionCodeEntity> collectionCodeEntities = new ArrayList<>(
            getCollectionCodeEntityByElement(collectReportReqDto.getBizId(), collectReportReqDto.getCurrentOperate().getSiteCode(), null));

        //查询所有统计数据
        List<CollectionCounter> allCounter = new ArrayList<>(
            collectionRecordService.sumCollectionByCollectionCode(collectionCodeEntities, CollectionAggCodeTypeEnum.waybill_code));

        Map<String, List<CollectionCodeEntity>> collectionCodeMap = collectionCodeEntities.parallelStream()
            .collect(Collectors.groupingBy(CollectionCodeEntity::getCollectionCode));

        //处理集齐集合的类型字段
        List<CollectionCounter> collectedCounters = allCounter.parallelStream()
                    .peek(
                        collectionCounter ->
                            collectionCounter.setBusinessType(
                                collectionCodeMap.get(collectionCounter.getCollectionCode()).get(0).getBusinessType())
                    )
                    .collect(Collectors.toList());

        //不齐数量
        CollectReportStatisticsDto noneCollected = new CollectReportStatisticsDto();
        noneCollected.setCollectType(CollectTypeEnum.WAYBILL_BUQI.getCode());
        noneCollected.setStatisticsNum(
            allCounter.parallelStream().mapToInt(CollectionCounter::getNoneCollectedNum).sum()
        );
        res.add(noneCollected);


        //本车集齐数量
        CollectReportStatisticsDto taskCollected = new CollectReportStatisticsDto();
        taskCollected.setCollectType(CollectTypeEnum.TASK_JIQI.getCode());
        taskCollected.setStatisticsNum(
            collectedCounters.parallelStream()
                .filter(
                    collectionCounter ->
                        CollectionBusinessTypeEnum.unload_collection.equals(collectionCounter.getBusinessType())
                )
                .mapToInt(CollectionCounter::getCollectedNum)
                .sum()
        );
        res.add(taskCollected);

        //在库集齐数量
        CollectReportStatisticsDto siteCollected = new CollectReportStatisticsDto();
        siteCollected.setCollectType(CollectTypeEnum.SITE_JIQI.getCode());
        siteCollected.setStatisticsNum(
            collectedCounters.parallelStream()
                .filter(
                    collectionCounter ->
                        CollectionBusinessTypeEnum.all_site_collection.equals(collectionCounter.getBusinessType())
                )
                .mapToInt(CollectionCounter::getCollectedNum)
                .sum()
        );
        res.add(siteCollected);

        return res;

    }

    /**
     * 根据封车编码和站点查询待集齐集合的ID列表
     * @param bizCode
     * @param siteCode
     * @param businessType
     * @return
     */
    public List<CollectionCodeEntity> getCollectionCodeEntityByElement (String bizCode, Integer siteCode, CollectionBusinessTypeEnum businessType) {
        String dateNow = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String dateNow_1 = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<String> dates = Arrays.asList(dateNow, dateNow_1);
        return dates.parallelStream().flatMap((Function<String, Stream<CollectionCodeEntity>>)s -> {
            Map<CollectionConditionKeyEnum, Object> element = new HashMap<>();
            element.put(CollectionConditionKeyEnum.site_code, siteCode);
            element.put(CollectionConditionKeyEnum.seal_car_code, bizCode);
            element.put(CollectionConditionKeyEnum.date_time, s);

            return collectionRecordService.queryAllCollectionCodesByElement(element, businessType).stream();
        }).collect(Collectors.toList());
    }

    /**
     * 获取场地类型具体实现 bean
     * @param collectDto
     * @return
     */
    private CollectSiteTypeService getCollectSiteTypeService(CollectDto collectDto) {
        return CollectSiteTypeServiceFactory.getCollectSiteTypeService(
                parseSiteType(collectDto.getWaybillCode(), collectDto.getNextSiteCode()));
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.parseSiteType",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public Integer parseSiteType(String waybillCode, Integer siteCode) {
        //是末级场地
        if(this.isEndSite(waybillCode, siteCode)){
            return CollectSiteTypeEnum.WAYBILL.getCode();
        }
        //中转场地
        return CollectSiteTypeEnum.HANDOVER.getCode();
    }

    /**
     * 是否末端场地
     * @param waybillCode
     * @param siteCode
     * @return
     */
    private boolean isEndSite(String waybillCode, Integer siteCode) {
        BaseEntity<Waybill> waybillRes = waybillQueryManager.getWaybillByWaybillCode(waybillCode);
        if (waybillRes == null || waybillRes.getResultCode() != 1 || waybillRes.getData() == null) {
            log.error("JyCollectServiceImpl.isEndSite:运单不存在：{}" , JsonHelper.toJson(waybillCode));
            throw new JyBizException(waybillCode+"该运单信息不存在");
        }
        Waybill waybill = waybillRes.getData();
        if(waybill == null) {
            log.error("JyCollectServiceImpl.isEndSite:运单waybill不存在：{}" , JsonHelper.toJson(waybillCode));
            throw new JyBizException(waybillCode+"该运单信息不存在");
        }
        Integer oldSiteId = waybill.getOldSiteId();
        if(oldSiteId == null) {
            log.warn("JyCollectServiceImpl.isEndSite:运单{}预分拣站点查询为空waybill={}", waybillCode, JsonHelper.toJson(waybill));
            return false;
        }

        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(oldSiteId);
        if (baseSite == null || baseSite.getDmsId() == null) {
            log.warn("JyCollectServiceImpl.isEndSite:根据站点【{}】获取站点信息为空,site={}", oldSiteId, JsonHelper.toJson(baseSite));
            return false;
        }
        return  baseSite.getDmsId().equals(siteCode);
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.initCollect",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean initCollect(CollectDto collectDto, List<CollectionScanCodeEntity> packageCodeList) {
        String methodDesc = "JyCollectServiceImpl.initCollect:集齐初始化:";
        CollectionCreatorEntity collectionCreatorEntity = getCollectionCreatorEntity(collectDto, packageCodeList);

        //根据中转和末端的场景决定是否需要从运单获取全量包裹数据
        if (CollectionBusinessTypeEnum.all_site_collection.equals(collectionCreatorEntity.getCollectionCodeEntity().getBusinessType())) {
            List<DeliveryPackageD> packageDList = waybillQueryManager.findWaybillPackList(collectDto.getWaybillCode());
            if (CollectionUtils.isNotEmpty(packageDList)) {
                Map<String, List<CollectionScanCodeEntity>> scanCodeMap = packageCodeList.parallelStream().collect(Collectors.groupingBy(CollectionScanCodeEntity::getScanCode));

                packageCodeList.addAll(packageDList.parallelStream()
                    .filter(packageD -> !scanCodeMap.containsKey(packageD.getPackageBarcode()))
                    .map(packageD -> {
                        CollectionScanCodeEntity collectionScanCodeEntity = new CollectionScanCodeEntity();
                        collectionScanCodeEntity.setScanCode(packageD.getPackageBarcode());
                        collectionScanCodeEntity.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
                        collectionScanCodeEntity.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code, collectDto.getWaybillCode()));
                        return collectionScanCodeEntity;
                    }).collect(Collectors.toList())
                );

            }
        }
        Result<Boolean> errMsgRes = new Result<>();
        if(!collectionRecordService.initPartCollection(collectionCreatorEntity, errMsgRes)) {
            log.error("{}集齐初始化错误，req={},res={}", methodDesc, JsonHelper.toJson(collectionCreatorEntity), JsonHelper.toJson(errMsgRes));
            return false;
        }
        return true;
    }

    /**
     * {
     *   "collectionCodeEntity" : {
     *     "businessType" : "unload_collection",
     *     "collectElements" : {
     *       "site_code" : "910",
     *       "seal_car_code" : "SC2412341231231",
     *       "date_time" : "2023-03-13"
     *     }
     *   },
     *   "collectionAggMarks" : {
     *     "JD0093356842901" : "39"
     *   },
     *   "collectionScanCodeEntities" : [ {
     *     "scanCode" : "JD0093356842901-2-3-",
     *     "scanCodeType" : "package_code",
     *     "collectedMark" : "SC2412341231231",
     *     "collectionAggCodeMaps" : {
     *       "waybill_code" : "JD0093356842901"
     *     }
     *   } ]
     * }
     * @param collectDto
     * @param packageCodeList
     * @return
     */
    private CollectionCreatorEntity getCollectionCreatorEntity(CollectDto collectDto, List<CollectionScanCodeEntity> packageCodeList) {
        CollectionCreatorEntity entity = new CollectionCreatorEntity();
        entity.setCollectionCodeEntity(getCollectionCodeEntity(collectDto));
        entity.setCollectionAggMarks(getNextSiteCodeMap(collectDto));
        entity.setCollectionScanCodeEntities(packageCodeList);
        return entity;
    }

    //集齐初始化参数组装
    private CollectionCodeEntity getCollectionCodeEntity(CollectDto collectDto) {
        CollectionCodeEntity collectionCodeEntity = buildCollectionCodeEntity(collectDto);
        String collectionCode = collectionRecordService.getJQCodeByBusinessType(collectionCodeEntity, collectDto.getOperatorErp());
        if(log.isInfoEnabled()) {
            log.info("JyCollectServiceImpl.getCollectionCodeEntity获取collectionCode，参数collectionCodeEntity={},res={}", JsonHelper.toJson(collectionCodeEntity), collectionCode);
        }
        collectionCodeEntity.setCollectionCode(collectionCode);
        return collectionCodeEntity;
    }
    //集齐初始化参数组装
    private Map<String, String> getNextSiteCodeMap(CollectDto collectDto) {
        Integer nextSiteId = null;
        if(collectDto.getNextSiteCode() == null) {
            nextSiteId = waybillService.getRouterFromMasterDb(collectDto.getWaybillCode(), collectDto.getCollectNodeSiteCode());
        }
        if(nextSiteId == null) {
            log.warn("JyCollectServiceImpl.getNextSiteCodeMap集齐运单查询下游流向为空，reqDto={}", JsonHelper.toJson(collectDto));
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        map.put(collectDto.getWaybillCode(), nextSiteId.toString());
        return map;

    }
    //集齐初始化参数组装
    private CollectionCodeEntity buildCollectionCodeEntity(CollectDto req) {
        Map<CollectionConditionKeyEnum, Object> collectElements = new HashMap<>();
        collectElements.put(CollectionConditionKeyEnum.site_code, req.getCollectNodeSiteCode());
        collectElements.put(CollectionConditionKeyEnum.seal_car_code, req.getBizId());
        collectElements.put(CollectionConditionKeyEnum.date_time, DateUtil.format(new Date(), DateUtil.FORMAT_DATE));

        CollectionBusinessTypeEnum collectionBusinessTypeEnum = CollectionBusinessTypeEnum.unload_collection;
        if(this.isEndSite(req.getWaybillCode(), req.getCollectNodeSiteCode())) {
            collectionBusinessTypeEnum = CollectionBusinessTypeEnum.all_site_collection;
        }
        CollectionCodeEntity collectionCodeEntity = new CollectionCodeEntity(collectionBusinessTypeEnum);
        collectionCodeEntity.addAllKey(collectElements);
        return collectionCodeEntity;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.initAndCollectedPartCollection",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean initAndCollectedPartCollection(CollectDto collectDto, List<CollectionScanCodeEntity> packageCodeList) {
        String methodDesc = "JyCollectServiceImpl.initAndCollectedPartCollection:集齐初始化&修改机器状态:";
        CollectionCreatorEntity collectionCreatorEntity = getCollectionCreatorEntity(collectDto, packageCodeList);
        Result<Boolean> errMsgRes = new Result<>();
        if(!collectionRecordService.initAndCollectedPartCollection(collectionCreatorEntity, errMsgRes)) {
            log.error("{}集齐初始化错误，req={},res={}", methodDesc, JsonHelper.toJson(collectionCreatorEntity), JsonHelper.toJson(errMsgRes));
            return false;
        }
        return true;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.removeCollect",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean removeCollect(CollectDto collectDto) {
        //todo  直接调用集齐服务初始化，集齐服务处理场地是末端还是中转
        return true;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.updateSingleCollectStatus",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean updateSingleCollectStatus(UnloadScanCollectDealDto unloadScanCollectDealDto) {
        Map<CollectionConditionKeyEnum, Object> collectElements = new HashMap<>();
        collectElements.put(CollectionConditionKeyEnum.site_code, unloadScanCollectDealDto.getCurrentOperate().getSiteCode());
        collectElements.put(CollectionConditionKeyEnum.seal_car_code, unloadScanCollectDealDto.getBizId());
        collectElements.put(CollectionConditionKeyEnum.date_time, DateUtil.format(new Date(), DateUtil.FORMAT_DATE));
        //
        CollectionScanCodeEntity collectionScanCodeEntity = new CollectionScanCodeEntity();
        collectionScanCodeEntity.setScanCode(unloadScanCollectDealDto.getScanCode());
        collectionScanCodeEntity.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
        collectionScanCodeEntity.setCollectedMark(unloadScanCollectDealDto.getBizId());
        collectionScanCodeEntity.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code, WaybillUtil.getWaybillCode(unloadScanCollectDealDto.getScanCode())));
        //
        CollectionCollectorEntity collectionCollectorEntity = new CollectionCollectorEntity();
        collectionCollectorEntity.setCollectElements(collectElements);
        collectionCollectorEntity.setCollectionScanCodeEntity(collectionScanCodeEntity);
        //
        Result<Boolean> result = new Result<>();
        if(!collectionRecordService.collectTheScanCode(collectionCollectorEntity, result)) {
            log.error("JyCollectServiceImpl.updateSingleCollectStatus：修改集齐明细状态异常，req={},res={}", JsonHelper.toJson(collectionCollectorEntity), JsonHelper.toJson(result));
            return false;
        }
        return true;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.scanQueryCollectTypeStatistics",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public CollectReportStatisticsDto scanQueryCollectTypeStatistics(UnloadScanCollectDealDto reqDto) {
        String methodDesc = "JyCollectServiceImpl.scanQueryCollectTypeStatistics：实操扫描查询统计：";
        List<CollectionCodeEntity> collectionCodeEntityList = this.getCollectionCodeEntityByElement(reqDto.getBizId(), reqDto.getCurrentOperate().getSiteCode(), null);
        String waybillCode = WaybillUtil.getWaybillCode(reqDto.getScanCode());
        CollectionAggCodeTypeEnum typeEnum = CollectionAggCodeTypeEnum.waybill_code;
        CollectionAggCodeCounter collectionAggCodeCounter = collectionRecordService.countCollectionStatusByAggCodeAndCollectionCodeWithCollectedMark(
                collectionCodeEntityList, waybillCode, typeEnum, reqDto.getBizId());
        if(log.isInfoEnabled()) {
            log.info("{}查询集齐服务,param1={},param2={},param3={},param4={},res={}",
                    methodDesc, JsonHelper.toJson(collectionCodeEntityList), waybillCode, typeEnum, reqDto.getBizId(), JsonHelper.toJson(collectionAggCodeCounter));
        }
        CollectReportStatisticsDto resDto = new CollectReportStatisticsDto();
        if(collectionAggCodeCounter == null) {
            log.warn("{}查询集齐服务为空,reqDto={}", methodDesc, JsonHelper.toJson(resDto));
            return null;
        }
        //本车实际扫
        resDto.setActualScanNum(
                (Objects.isNull(collectionAggCodeCounter.getInnerMarkCollectedNum()) ? 0 : collectionAggCodeCounter.getInnerMarkCollectedNum())
                + (Objects.isNull(collectionAggCodeCounter.getInnerMarkExtraCollectedNum()) ? 0 : collectionAggCodeCounter.getInnerMarkExtraCollectedNum()));
        //是否被初始化过：没有被初始化过，说明是多扫，走在库集齐逻辑
        boolean taskExistInitFlag = Objects.isNull(collectionAggCodeCounter.getSumScanNum()) || collectionAggCodeCounter.getSumScanNum() <= 0;
        if(!taskExistInitFlag) {
            resDto.setTaskExistInitFlag(false);
            resDto.setCollectType(CollectTypeEnum.SITE_JIQI.getCode());
            return resDto;
        }
        if(collectionAggCodeCounter.getNoneCollectedNum() > 0) {
            resDto.setCollectType(CollectTypeEnum.WAYBILL_BUQI.getCode());
            resDto.setStatisticsNum(collectionAggCodeCounter.getNoneCollectedNum());
        }else if(collectionAggCodeCounter.getOutMarkCollectedNum() > 0 ) {
            resDto.setCollectType(CollectTypeEnum.SITE_JIQI.getCode());
            resDto.setStatisticsNum(collectionAggCodeCounter.getOutMarkCollectedNum());
        }else {
            resDto.setCollectType(CollectTypeEnum.WAYBILL_BUQI.getCode());
            resDto.setStatisticsNum(collectionAggCodeCounter.getInnerMarkCollectedNum());
        }
        return resDto;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.waybillBatchUpdateCollectStatus",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean waybillBatchUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto) {
        String methodDesc = "JyCollectServiceImpl.waybillBatchUpdateCollectStatus:运单批量修改集齐状态：";

        if(!jyCollectCacheService.lockSaveTaskNullWaybillUpdateCollectStatus(paramDto)) {
            if(log.isInfoEnabled()) {
                log.info("{}未获取到锁，说明程序已经处理中，不在处理，paramDto={}", methodDesc, JsonHelper.toJson(paramDto));
            }
            return true;
        }
        try{
            if(jyCollectCacheService.cacheExistTaskNullWaybillUpdateCollectStatus(paramDto)) {
                if(log.isInfoEnabled()) {
                    log.info("{}防重缓存已存在，不在处理，paramDto={}", methodDesc, JsonHelper.toJson(paramDto));
                }
                return true;
            }
            Map<CollectionConditionKeyEnum, Object> collectElements = new HashMap<>();
            collectElements.put(CollectionConditionKeyEnum.site_code, paramDto.getScanSiteCode());
            collectElements.put(CollectionConditionKeyEnum.seal_car_code, paramDto.getBizId());
            collectElements.put(CollectionConditionKeyEnum.date_time, DateUtil.format(new Date(), DateUtil.FORMAT_DATE));
            //
            String waybillCode = WaybillUtil.getWaybillCode(paramDto.getScanCode());
            List<String> packageCodeList = getPageNoPackageCodeListFromWaybill(waybillCode, paramDto.getPageNo(), paramDto.getPageSize());
            for (String pCode : packageCodeList) {
                CollectionScanCodeEntity collectionScanCodeEntity = new CollectionScanCodeEntity();
                collectionScanCodeEntity.setScanCode(pCode);
                collectionScanCodeEntity.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
                collectionScanCodeEntity.setCollectedMark(paramDto.getBizId());

                //
                CollectionCollectorEntity reqEntity = new CollectionCollectorEntity();
                reqEntity.setCollectElements(collectElements);
                reqEntity.setCollectionScanCodeEntity(collectionScanCodeEntity);
                Result<Boolean> errMsgRes = new Result<>();
                if(!collectionRecordService.collectTheScanCode(reqEntity, errMsgRes)) {
                    log.error("{}, 运单循环调用修改包裹集齐数据异常，方法请求Dto={}, 异常包裹参数={}，errMsgRes={}",
                            methodDesc, JsonHelper.toJson(paramDto), JsonHelper.toJson(reqEntity), JsonHelper.toJson(errMsgRes));
                    throw new JyBizException("运单循环调用修改包裹集齐数据异常" + errMsgRes.getMessage());
                }
            }
            jyCollectCacheService.cacheSaveTaskNullWaybillUpdateCollectStatus(paramDto);
            return true;
        }catch (Exception e) {
            throw new JyBizException("运单批次循环调用修改包裹集齐数据异常" + e.getMessage());
        }finally {
            jyCollectCacheService.lockDelTaskNullWaybillUpdateCollectStatus(paramDto);
        }
    }

    public List<String> getPageNoPackageCodeListFromWaybill(String waybillCode, int pageNo, int pageSize) {
        // 分页查询包裹数据
        BigWaybillDto bigWaybillDto = getWaybill(waybillCode, pageNo, pageSize);
        List<String> res = new ArrayList<>();
        List<DeliveryPackageD> packages=bigWaybillDto.getPackageList();
        for (DeliveryPackageD pack : packages) {
            res.add(pack.getPackageBarcode());
        }
        return res;
    }

    /**
     * 分页获取包裹数据
     * @param waybillCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    private BigWaybillDto getWaybill(String waybillCode, int pageNo, int pageSize) {

        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(false);
        wChoice.setQueryWaybillM(true); // 查询waybillState

        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);
        BigWaybillDto bigWaybillDto = null;
        if (baseEntity != null && baseEntity.getData()!= null) {
            bigWaybillDto = baseEntity.getData();
            BaseEntity<List<DeliveryPackageD>> pageLists =
                    this.waybillPackageManager.getPackListByWaybillCodeOfPage(waybillCode, pageNo, pageSize);
            if (pageLists != null && pageLists.getData() != null ) {
                bigWaybillDto.setPackageList(pageLists.getData());
            }
        }
        return bigWaybillDto;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.batchUpdateCollectStatusWaybillSplit",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean batchUpdateCollectStatusWaybillSplit(BatchUpdateCollectStatusDto paramDto) {
        String methodDesc = "JyCollectServiceImpl.batchUpdateCollectStatusWaybillSplit:运单批量修改集齐状态按运单拆分：";

        if(!jyCollectCacheService.lockSaveTaskNullWaybillSplitUpdateCollectStatus(paramDto)) {
            if(log.isInfoEnabled()) {
                log.info("{}未获取到锁，说明程序已经处理中，不在处理，paramDto={}", methodDesc, JsonHelper.toJson(paramDto));
            }
            return true;
        }
        try{
            if(jyCollectCacheService.cacheExistTaskNullWaybillSplitUpdateCollectStatus(paramDto)) {
                if(log.isInfoEnabled()) {
                    log.info("{}防重缓存已存在，不在处理，paramDto={}", methodDesc, JsonHelper.toJson(paramDto));
                }
                return true;
            }
            //
            BigWaybillDto bigWaybillDto = getWaybillPackage(WaybillUtil.getWaybillCode(paramDto.getScanCode()));
            if (bigWaybillDto == null || CollectionUtils.isEmpty(bigWaybillDto.getPackageList())) {
                log.warn("{}运单{}查询包裹为空, paramDto:[{}]，bigWaybillDto={}", methodDesc, paramDto.getScanCode(), JsonHelper.toJson(paramDto), JsonHelper.toJson(bigWaybillDto));
                return false;
            }
            int collectOneBatchSize = CollectServiceConstant.COLLECT_INIT_BATCH_DEAL_SIZE;
            int totalPackageNum = bigWaybillDto.getPackageList().size();
            int collectBatchPageTotal = (totalPackageNum % collectOneBatchSize) == 0 ? (totalPackageNum / collectOneBatchSize) : (totalPackageNum / collectOneBatchSize) + 1;

            List<Message> messageList = new ArrayList<>();
            for (int pageNo = 1; pageNo <= collectBatchPageTotal; pageNo++) {
                BatchUpdateCollectStatusDto mqDto = new BatchUpdateCollectStatusDto();
                BeanUtils.copyProperties(paramDto, mqDto);
                mqDto.setPageNo(pageNo);
                mqDto.setPageSize(collectOneBatchSize);
                String msgText = JsonUtils.toJSONString(mqDto);
                if(log.isInfoEnabled()) {
                    log.info("{}.{}：jmq消息, msg={}", methodDesc, jyCollectBatchUpdateProducer.getTopic(), msgText);
                }
                //todo 批量发jmq
                messageList.add(new Message(jyCollectBatchUpdateProducer.getTopic(),msgText,getBusinessId(mqDto)));
            }
            jyCollectBatchUpdateProducer.batchSendOnFailPersistent(messageList);
            jyCollectCacheService.cacheSaveTaskNullWaybillSplitUpdateCollectStatus(paramDto);
            return true;
        }catch (Exception e) {
            throw new JyBizException("修改包裹集齐数据拆分运单逻辑异常" + e.getMessage());
        }finally {
            jyCollectCacheService.lockDelTaskNullWaybillSplitUpdateCollectStatus(paramDto);
        }
    }

    public String getBusinessId(BatchUpdateCollectStatusDto mqDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(mqDto.getBizId()).append(":")
                .append(mqDto.getScanCode()).append(":")
                .append(mqDto.getPageNo()).append(":")
                .append(mqDto.getPageSize());
        return sb.toString();
    }

    private BigWaybillDto getWaybillPackage(String waybillCode) {
        BigWaybillDto result = null;
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,true, false, true, true);
        if (baseEntity != null) {
            result = baseEntity.getData();
        }
        if (log.isInfoEnabled()){
            log.info(MessageFormat.format("获取运单信息{0}, 结果为{1}", waybillCode, JsonHelper.toJson(result)));
        }

        return result;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.collectWaitWaybillNum",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ScanCollectStatisticsDto> collectWaitWaybillNum(CollectStatisticsQueryDto reqDto) {
        InvokeResult<ScanCollectStatisticsDto> res = new InvokeResult<>();
        res.success();
        Integer collectType = CollectTypeEnum.WAYBILL_BUQI.getCode();
//        CollectStatisticsQueryParamDto queryParamDto = new CollectStatisticsQueryParamDto();
//        queryParamDto.setBizId(reqDto.getBizId());
//        queryParamDto.setCollectType(collectType);
//        //
//        CollectStatisticsDimensionService collectStatisticsService = CollectStatisticsDimensionFactory.getCollectStatisticsDimensionService(collectType);
//        CollectReportStatisticsDto collectReportStatisticsDto = collectStatisticsService.collectStatistics(queryParamDto);
        List<CollectionCodeEntity> collectionCodeEntities = this.getCollectionCodeEntityByElement(reqDto.getBizId(), reqDto.getCurrentOperate().getSiteCode(), null);
        Integer waybillBuQiNum =
            collectionRecordService.countNoneCollectedAggCodeNumByCollectionCode(collectionCodeEntities,
                CollectionAggCodeTypeEnum.waybill_code, reqDto.getBizId());
        if(waybillBuQiNum == null) {
            log.warn("JyCollectServiceImpl.collectWaitWaybillNum：查不齐运单数量返回空,服务req={}，查询参数={}", JsonHelper.toJson(reqDto), JsonHelper.toJson(collectionCodeEntities));
            waybillBuQiNum = 0;
        }
        ScanCollectStatisticsDto resData = new ScanCollectStatisticsDto();
        resData.setCollectType(collectType);
        resData.setWaybillBuQiNum(waybillBuQiNum);
        res.setData(resData);
        return res;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.findCollectReportByScanCode",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<CollectReportResDto> findCollectReportByScanCode(CollectReportQueryParamReqDto reqDto) {
        CollectReportReqDto param = new CollectReportReqDto();
        param.setWaybillCode(WaybillUtil.getWaybillCode(reqDto.getScanCode()));
        param.setCollectType(reqDto.getCollectType());
        param.setManualCreateTaskFlag(reqDto.getManualCreateTaskFlag());
        param.setUser(reqDto.getUser());
        param.setCurrentOperate(reqDto.getCurrentOperate());
        param.setBizId(reqDto.getBizId());
        param.setPageNo(1);
        param.setPageSize(30);
        return this.findCollectInfo(param);
    }

}
