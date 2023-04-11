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
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadCollectDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.constant.CollectServiceConstant;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectInitNodeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectSiteTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectSiteTypeServiceFactory;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectStatisticsDimensionFactory;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectSiteTypeService;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectStatisticsDimensionService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.*;
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
    @Autowired
    JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;
    @Autowired
    @Qualifier(value = "jyCollectDataInitSplitProducer")
    private DefaultJMQProducer jyCollectDataInitSplitProducer;
    @Autowired
    private RouterService routerService;

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.findCollectInfo",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<CollectReportResDto> findCollectInfo(CollectReportReqDto collectReportReqDto) {
        InvokeResult<CollectReportResDto> res = new InvokeResult<>();
        CollectReportResDto resData = new CollectReportResDto();
        res.setData(resData);
        resData.setCollectType(collectReportReqDto.getCollectType());
        resData.setManualCreateTaskFlag(false);
        //集齐运单列表
        CollectStatisticsDimensionService collectStatisticsService = CollectStatisticsDimensionFactory.getCollectStatisticsDimensionService(collectReportReqDto.getCollectType());
        resData.setCollectReportDtoList(collectStatisticsService.queryCollectListPage(collectReportReqDto, resData));
        //集齐统计数据
        resData.setCollectReportStatisticsDtoList(getCollectReportDetailPackageDtoList(collectReportReqDto));
        return res;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.findCollectDetail",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<CollectReportDetailResDto> findCollectDetail(CollectReportReqDto collectReportReqDto) {
        InvokeResult<CollectReportDetailResDto> res = new InvokeResult<>();
        CollectReportDetailResDto resData = new CollectReportDetailResDto();
        res.setData(resData);
        resData.setCollectType(collectReportReqDto.getCollectType());
        resData.setManualCreateTaskFlag(false);

        CollectStatisticsDimensionService collectStatisticsService = CollectStatisticsDimensionFactory.getCollectStatisticsDimensionService(collectReportReqDto.getCollectType());
        //当前运单统计
        CollectReportReqDto collectReportReqDto1 = new CollectReportReqDto();
        org.springframework.beans.BeanUtils.copyProperties(collectReportReqDto, collectReportReqDto1);
        collectReportReqDto1.setPageNo(1);
        collectReportReqDto1.setPageSize(30);
        List<CollectReportDto> collectReportDtoList = collectStatisticsService.queryCollectListPage(collectReportReqDto1, resData);
        if(CollectionUtils.isEmpty(collectReportDtoList)) {
            res.setMessage("未查到当前集齐类型的运单数据");
            return res;
        }
        resData.setCollectReportDto(collectReportDtoList.get(0));

        //集齐类型运单统计
//        resData.setCollectReportStatisticsDtoList(getCollectReportDetailPackageDtoList(collectReportReqDto));

        //明细分页查询
        resData.setCollectReportDetailPackageDtoList(collectStatisticsService.queryCollectDetail(collectReportReqDto, resData));
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
            getCollectionCodeEntityByElement(collectReportReqDto.getBizId(),
                collectReportReqDto.getCurrentOperate().getSiteCode(), Boolean.TRUE.equals(collectReportReqDto.getManualCreateTaskFlag())));

        //不齐数量
        CollectReportStatisticsDto noneCollected = new CollectReportStatisticsDto();
        noneCollected.setCollectType(CollectTypeEnum.WAYBILL_BUQI.getCode());
        noneCollected.setStatisticsNum(
            collectionRecordService.countNoneCollectedAggCodeNumByCollectionCode(collectionCodeEntities, CollectionAggCodeTypeEnum.waybill_code,
                collectReportReqDto.getWaybillCode(), collectReportReqDto.getBizId())
        );
        res.add(noneCollected);

        //本车集齐数量
        CollectReportStatisticsDto taskCollected = new CollectReportStatisticsDto();
        taskCollected.setCollectType(CollectTypeEnum.TASK_JIQI.getCode());
        taskCollected.setStatisticsNum(
            collectionRecordService.countCollectionAggCodeNumByCollectionCodeInnerMark(collectionCodeEntities, CollectionAggCodeTypeEnum.waybill_code,
                collectReportReqDto.getWaybillCode(), collectReportReqDto.getBizId())
        );
        res.add(taskCollected);

        //在库集齐数量
        CollectReportStatisticsDto siteCollected = new CollectReportStatisticsDto();
        siteCollected.setCollectType(CollectTypeEnum.SITE_JIQI.getCode());
        siteCollected.setStatisticsNum(
            collectionRecordService.countCollectionAggCodeNumByCollectionCodeOutMark(collectionCodeEntities, CollectionAggCodeTypeEnum.waybill_code,
                collectReportReqDto.getWaybillCode(), collectReportReqDto.getBizId())
        );
        res.add(siteCollected);

        if(log.isInfoEnabled()) {
            log.info("JyCollectServiceImpl.getCollectReportDetailPackageDtoList--PDA查询集齐报表三种集齐类型的统计数据，req={},res={}",
                    JsonUtils.toJSONString(collectReportReqDto), JsonUtils.toJSONString(res));
        }
        return res;

    }

    /**
     * 根据封车编码和站点查询待集齐集合的ID列表
     * @param bizCode
     * @param siteCode
     * @return
     */
    public List<CollectionCodeEntity> getCollectionCodeEntityByElement (String bizCode, Integer siteCode, boolean isManualCreateTask) {
        Map<CollectionConditionKeyEnum, Object> element = new HashMap<>();
        element.put(CollectionConditionKeyEnum.site_code, siteCode);
        element.put(CollectionConditionKeyEnum.seal_car_code, bizCode);

        //自建任务只会命中一个池子
        CollectionBusinessTypeEnum businessType = isManualCreateTask? CollectionBusinessTypeEnum.all_site_collection : null;

        List<CollectionCodeEntity> collectionCodeEntityList = collectionRecordService.queryAllCollectionCodesByElement(element, businessType);

        boolean isNotExistAllSite = collectionCodeEntityList.parallelStream()
            .noneMatch(collectionCodeEntity ->
                CollectionBusinessTypeEnum.all_site_collection.equals(collectionCodeEntity.getBusinessType())
                    && StringUtils.isNotEmpty(collectionCodeEntity.getCollectionCode()));

        if (CollectionUtils.isEmpty(collectionCodeEntityList)) {
            collectionCodeEntityList = new ArrayList<>();
        }

        if (CollectionUtils.isEmpty(collectionCodeEntityList) || isNotExistAllSite) {
            CollectionCodeEntity collectionCodeEntity = new CollectionCodeEntity(CollectionBusinessTypeEnum.all_site_collection);
            collectionCodeEntity.addAllKey(element);
            collectionCodeEntity.buildCollectionCondition();
            collectionCodeEntity.setCollectionCode(collectionRecordService.getCollectionCode(collectionCodeEntity, "NONE"));
            collectionCodeEntityList.add(collectionCodeEntity);
        }

        if (!isManualCreateTask) {
            boolean isNotExistUnloadTask = collectionCodeEntityList.parallelStream()
                .noneMatch(collectionCodeEntity ->
                    CollectionBusinessTypeEnum.unload_collection.equals(collectionCodeEntity.getBusinessType())
                        && StringUtils.isNotEmpty(collectionCodeEntity.getCollectionCode()));
            if (isNotExistUnloadTask) {
                CollectionCodeEntity collectionCodeEntity = new CollectionCodeEntity(CollectionBusinessTypeEnum.unload_collection);
                collectionCodeEntity.addAllKey(element);
                collectionCodeEntity.buildCollectionCondition();
                collectionCodeEntity.setCollectionCode(collectionRecordService.getCollectionCode(collectionCodeEntity, "NONE"));
                collectionCodeEntityList.add(collectionCodeEntity);
            }
        }
        return collectionCodeEntityList;
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
        log.info("JyCollectServiceImpl.isEndSite:运单【{}】的预分拣站点为【{}】，预分拣站点所书分拣中心为【{}】，是否末级：【{}】"
            , waybillCode, oldSiteId, siteCode, baseSite.getDmsId().equals(siteCode));
        return  baseSite.getDmsId().equals(siteCode);
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.initCollect",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean initCollect(CollectDto collectDto, List<CollectionScanCodeEntity> packageCodeList) {
        String methodDesc = "JyCollectServiceImpl.initCollect:集齐初始化:";
        CollectionCreatorEntity collectionCreatorEntity = getCollectionCreatorEntity(collectDto, packageCodeList, CollectionBusinessTypeEnum.all_site_collection);

        Result<Boolean> errMsgRes = new Result<>();
        if(!collectionRecordService.initPartCollection(collectionCreatorEntity, errMsgRes)) {
            log.error("{}集齐初始化错误，req={},res={}", methodDesc, JsonHelper.toJson(collectionCreatorEntity), JsonHelper.toJson(errMsgRes));
            return false;
        }
        return true;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.sealCarInitCollect",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean sealCarInitCollect(CollectDto collectDto, List<CollectionScanCodeEntity> packageCodeList) {
        String methodDesc = "JyCollectServiceImpl.sealCarInitCollect:集齐初始化:";
        if (isEndSite(collectDto.getWaybillCode(), collectDto.getCollectNodeSiteCode())) {

            sealCarWaybillCollectInitSendMq(collectDto);

            Result<Boolean> errMsgRes = new Result<>();
            CollectionCreatorEntity collectionCreatorEntity = getCollectionCreatorEntity(collectDto, packageCodeList, CollectionBusinessTypeEnum.all_site_collection);
            if(!collectionRecordService.initPartCollection(collectionCreatorEntity, errMsgRes)) {
                log.error("{}集齐初始化错误，req={},res={}", methodDesc, JsonHelper.toJson(collectionCreatorEntity), JsonHelper.toJson(errMsgRes));
                return false;
            }
        } else {
            Result<Boolean> errMsgRes = new Result<>();
            CollectionCreatorEntity collectionCreatorEntity = getCollectionCreatorEntity(collectDto, packageCodeList, CollectionBusinessTypeEnum.unload_collection);
            if(!collectionRecordService.initPartCollection(collectionCreatorEntity, errMsgRes)) {
                log.error("{}集齐初始化错误，req={},res={}", methodDesc, JsonHelper.toJson(collectionCreatorEntity), JsonHelper.toJson(errMsgRes));
                return false;
            }
        }

        return true;
    }


    /**
     * 生成任务初始化集齐对象，发送初始化jmq
     */
    @Override
    public void sealCarWaybillCollectInitSendMq(CollectDto collectDto) {

        InitCollectDto initCollectDto = new InitCollectDto();
        initCollectDto.setOperateTime(System.currentTimeMillis());
        initCollectDto.setBizId(collectDto.getBizId());
        initCollectDto.setOperateNode(CollectInitNodeEnum.SEAL_WAYBILL_INIT.getCode());
        initCollectDto.setCollectNodeSiteCode(collectDto.getCollectNodeSiteCode());
        initCollectDto.setWaybillCode(collectDto.getWaybillCode());
        initCollectDto.setSealBatchCode(collectDto.getSealBatchCode());
        //自建任务扫描初始化businessId是bizId + 扫描单号+ 扫描类型；  封车初始化businessId是bizId
        StringBuilder sb = new StringBuilder();
        String businessId = sb.append(initCollectDto.getBizId()).append(":").append(initCollectDto.getWaybillCode()).toString();
        String msg = com.jd.bluedragon.utils.JsonHelper.toJson(initCollectDto);
        if(log.isInfoEnabled()) {
            log.info("JyUnloadVehicleCheckTysService.sealCarWaybillCollectInitSendMq：封车时末端场地运单走按运单维度拆分集齐初始化逻辑，补偿末端未到数据集齐，发送JMQ, msg={}", msg);
        }
        jyCollectDataInitSplitProducer.sendOnFailPersistent(businessId, msg);
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
    private CollectionCreatorEntity getCollectionCreatorEntity(CollectDto collectDto, List<CollectionScanCodeEntity> packageCodeList, CollectionBusinessTypeEnum businessTypeEnum) {
        CollectionCreatorEntity entity = new CollectionCreatorEntity();
        entity.setCollectionCodeEntity(getCollectionCodeEntity(collectDto, businessTypeEnum));
        entity.setCollectionAggMarks(getNextSiteCodeMap(collectDto));
        entity.setCollectionScanCodeEntities(packageCodeList);
        return entity;
    }

    //集齐初始化参数组装
    private CollectionCodeEntity getCollectionCodeEntity(CollectDto collectDto, CollectionBusinessTypeEnum businessTypeEnum) {
        CollectionCodeEntity collectionCodeEntity = buildCollectionCodeEntity(collectDto, businessTypeEnum);
        String collectionCode = collectionRecordService.getCollectionCode(collectionCodeEntity, collectDto.getOperatorErp());
        if(log.isInfoEnabled()) {
            log.info("JyCollectServiceImpl.getCollectionCodeEntity获取collectionCode，参数collectionCodeEntity={},res={}", JsonHelper.toJson(collectionCodeEntity), collectionCode);
        }
        collectionCodeEntity.setCollectionCode(collectionCode);
        return collectionCodeEntity;
    }
    //集齐初始化参数组装
    private Map<String, String> getNextSiteCodeMap(CollectDto collectDto) {
        Integer nextSiteId = collectDto.getNextSiteCode();
        if(nextSiteId == null) {
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
    private CollectionCodeEntity buildCollectionCodeEntity(CollectDto req, CollectionBusinessTypeEnum businessTypeEnum) {
        Map<CollectionConditionKeyEnum, Object> collectElements = new HashMap<>();
        collectElements.put(CollectionConditionKeyEnum.site_code, req.getCollectNodeSiteCode());
        collectElements.put(CollectionConditionKeyEnum.seal_car_code, req.getBizId());
        collectElements.put(CollectionConditionKeyEnum.date_time, DateUtil.format(new Date(), DateUtil.FORMAT_DATE));
        CollectionCodeEntity collectionCodeEntity = new CollectionCodeEntity(businessTypeEnum);
        collectionCodeEntity.addAllKey(collectElements);
        collectionCodeEntity.buildCollectionCondition();
        return collectionCodeEntity;
    }

    /**
     * 判断是否卸车无任务
     * @param bizId
     */
    private boolean isUnloadCarTaskNull(String bizId){
        JyBizTaskUnloadVehicleEntity unloadVehicleEntity = jyBizTaskUnloadVehicleService.findByBizId(bizId);
        if(unloadVehicleEntity != null && unloadVehicleEntity.getManualCreatedFlag().equals(1)) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.initAndCollectedPartCollection",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean initAndCollectedPartCollection(CollectDto collectDto, List<CollectionScanCodeEntity> packageCodeList) {
        String methodDesc = "JyCollectServiceImpl.initAndCollectedPartCollection:集齐初始化&修改机器状态:";
        CollectionCreatorEntity collectionCreatorEntity = getCollectionCreatorEntity(collectDto, packageCodeList, CollectionBusinessTypeEnum.all_site_collection);
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
        //自建任务只需要更新场地维度的集齐数据
        if (Boolean.FALSE.equals(unloadScanCollectDealDto.getManualCreateTaskFlag())) {
            collectElements.put(CollectionConditionKeyEnum.seal_car_code, unloadScanCollectDealDto.getBizId());
        }
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
        if(!Objects.isNull(unloadScanCollectDealDto.getNextSiteId())) {
            collectionCollectorEntity.setAggMark(unloadScanCollectDealDto.getNextSiteId().toString());
        }
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
    public UnloadCollectDto scanQueryCollectTypeStatistics(UnloadScanCollectDealDto reqDto) {
        String methodDesc = "JyCollectServiceImpl.scanQueryCollectTypeStatistics：实操扫描查询统计：";
        if(log.isInfoEnabled()) {
            log.info("{}入参={}", methodDesc, JsonHelper.toJson(reqDto));
        }
        //自建任务只能命中一个池子，非自建任务可能命中两个池子
        List<CollectionCodeEntity> collectionCodeEntityList = this.getCollectionCodeEntityByElement(reqDto.getBizId()
            , reqDto.getCurrentOperate().getSiteCode(), Boolean.TRUE.equals(reqDto.getManualCreateTaskFlag()));

        String waybillCode = WaybillUtil.getWaybillCode(reqDto.getScanCode());
        CollectionAggCodeTypeEnum typeEnum = CollectionAggCodeTypeEnum.waybill_code;

        CollectionAggCodeCounter collectionAggCodeCounter = null;
        if (Boolean.TRUE.equals(reqDto.getManualCreateTaskFlag())) {
            //自建任务去一个场地的池子中查询
            collectionAggCodeCounter = collectionRecordService.sumCollectionByAggCodeAndCollectionCode(collectionCodeEntityList.get(0),waybillCode, typeEnum, reqDto.getBizId());
        } else {

            CollectionCodeEntity allSiteCodeEntity = collectionCodeEntityList.parallelStream()
                .filter(collectionCodeEntity -> CollectionBusinessTypeEnum.all_site_collection.equals(collectionCodeEntity.getBusinessType())).findAny().orElse(null);
            //非自建任务去场地和任务的两个池子中查询
            List<CollectionAggCodeCounter> collectionAggCodeCounters = collectionRecordService.sumCollectionByAggCodeAndCollectionCode(
                collectionCodeEntityList,allSiteCodeEntity, waybillCode, typeEnum, reqDto.getBizId());
            //检查数据的有效性
            if (CollectionUtils.isNotEmpty(collectionAggCodeCounters) && collectionAggCodeCounters.size() == 1) {
                collectionAggCodeCounter = collectionAggCodeCounters.get(0);
            } else if (CollectionUtils.isNotEmpty(collectionAggCodeCounters) && collectionAggCodeCounters.size() > 1) {
                collectionAggCodeCounter = collectionAggCodeCounters.parallelStream()
                    .filter(item -> CollectionBusinessTypeEnum.unload_collection.equals(item.getBusinessType())).findAny()
                    .orElse(collectionAggCodeCounters.get(0));
            }
        }





        if(log.isInfoEnabled()) {
            log.info("{}查询集齐服务,参数={},运单号={},bizId={},集齐服务返回统计结果res={}",
                    methodDesc, JsonHelper.toJson(collectionCodeEntityList), waybillCode, reqDto.getBizId(), JsonHelper.toJson(collectionAggCodeCounter));
        }
        if(collectionAggCodeCounter == null) {
            log.warn("{}查询集齐服务为空,reqDto={}", methodDesc, JsonHelper.toJson(reqDto));
            return null;
        }
//        CollectReportStatisticsDto resDto = new CollectReportStatisticsDto();
        UnloadCollectDto unloadCollectDto = new UnloadCollectDto();
        unloadCollectDto.setWaybillCode(waybillCode);

        if(!NumberHelper.isPositiveNumber(collectionAggCodeCounter.getSumScanNum())
            && NumberHelper.gt(reqDto.getGoodNumber(), collectionAggCodeCounter.getExtraCollectedNum())) {
            //初始化数据未进来 且 运单包裹数量大于多扫数量，则表示运单不齐
            unloadCollectDto.setCollectType(CollectTypeEnum.WAYBILL_BUQI.getCode());
            unloadCollectDto.setCollectStatisticsNum(reqDto.getGoodNumber() - collectionAggCodeCounter.getExtraCollectedNum());
        } else if (!NumberHelper.isPositiveNumber(collectionAggCodeCounter.getSumScanNum())
            && NumberHelper.lte(reqDto.getGoodNumber(), collectionAggCodeCounter.getExtraCollectedNum())){
            //初始化数据未进来 且 运单包裹数量小于等于多扫数量，则表示运单集齐
            //运单集齐分为本车集齐和在库集齐
            if (NumberHelper.isPositiveNumber(collectionAggCodeCounter.getOutMarkExtraCollectedNum())) {
                unloadCollectDto.setCollectType(CollectTypeEnum.SITE_JIQI.getCode());
                unloadCollectDto.setCollectStatisticsNum(collectionAggCodeCounter.getOutMarkExtraCollectedNum());
            } else {
                unloadCollectDto.setCollectType(CollectTypeEnum.TASK_JIQI.getCode());
                unloadCollectDto.setCollectStatisticsNum(reqDto.getGoodNumber());
            }
        } else if(collectionAggCodeCounter.getNoneCollectedNum() > 0) {
            //已经初始化 未集数量大于0表述不齐
            unloadCollectDto.setCollectType(CollectTypeEnum.WAYBILL_BUQI.getCode());
            unloadCollectDto.setCollectStatisticsNum(collectionAggCodeCounter.getNoneCollectedNum());
        } else if(collectionAggCodeCounter.getOutMarkCollectedNum() > 0 ) {
            //已经初始化，且未集数量=0 表述集齐，其余其余车集齐大于0表示在库集齐
            unloadCollectDto.setCollectType(CollectTypeEnum.SITE_JIQI.getCode());
            unloadCollectDto.setCollectStatisticsNum(collectionAggCodeCounter.getOutMarkCollectedNum());
        } else {
            //已经初始化，且未集数量=0 表述集齐，其余其余车集齐大于0表示在库集齐
            unloadCollectDto.setCollectType(CollectTypeEnum.TASK_JIQI.getCode());
            unloadCollectDto.setCollectStatisticsNum(collectionAggCodeCounter.getSumScanNum() + collectionAggCodeCounter.getExtraCollectedNum());
        }
        return unloadCollectDto;
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
                collectionScanCodeEntity.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code, waybillCode));

                //
                CollectionCollectorEntity reqEntity = new CollectionCollectorEntity();
                reqEntity.setCollectElements(collectElements);
                reqEntity.setCollectionScanCodeEntity(collectionScanCodeEntity);
                RouteNextDto routeNextDto = routerService.matchNextNodeAndLastNodeByRouter(paramDto.getScanSiteCode(), waybillCode, null);
                if(!Objects.isNull(routeNextDto) && !Objects.isNull(routeNextDto.getFirstNextSiteId())) {
                    reqEntity.setAggMark(routeNextDto.getFirstNextSiteId().toString());
                }
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
        List<CollectionCodeEntity> collectionCodeEntities = this.getCollectionCodeEntityByElement(reqDto.getBizId(), reqDto.getCurrentOperate().getSiteCode(), Boolean.TRUE.equals(reqDto.getManualCreateTaskFlag()));
        Integer waybillBuQiNum =
            collectionRecordService.countNoneCollectedAggCodeNumByCollectionCode(collectionCodeEntities,
                CollectionAggCodeTypeEnum.waybill_code, null, reqDto.getBizId());
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
//        param.setManualCreateTaskFlag(reqDto.getManualCreateTaskFlag());
        param.setUser(reqDto.getUser());
        param.setCurrentOperate(reqDto.getCurrentOperate());
        param.setBizId(reqDto.getBizId());
        param.setPageNo(1);
        param.setPageSize(30);
        return this.findCollectInfo(param);
    }

}
