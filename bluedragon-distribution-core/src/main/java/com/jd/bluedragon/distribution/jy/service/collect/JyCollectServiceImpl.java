package com.jd.bluedragon.distribution.jy.service.collect;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collection.entity.CollectionCodeEntity;
import com.jd.bluedragon.distribution.collection.entity.CollectionCreatorEntity;
import com.jd.bluedragon.distribution.collection.entity.CollectionScanCodeEntity;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionScanCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.service.CollectionRecordService;
import com.jd.bluedragon.distribution.jy.dto.collect.*;
import com.jd.bluedragon.distribution.jy.dto.unload.CollectStatisticsQueryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanCollectStatisticsDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectSiteTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectSiteTypeServiceFactory;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectStatisticsDimensionFactory;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectSiteTypeService;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectStatisticsDimensionService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @Author zhengchengfa
 * @Description //集齐服务实现
 * @date
 **/
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


    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.findCollectInfo",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<CollectReportResDto> findCollectInfo(CollectReportReqDto collectReportReqDto) {
        InvokeResult<CollectReportResDto> res = new InvokeResult<>();
        CollectReportResDto resData = new CollectReportResDto();
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

        //集齐类型运单统计
        resData.setCollectReportStatisticsDtoList(getCollectReportDetailPackageDtoList(collectReportReqDto));

        CollectStatisticsDimensionService collectStatisticsService = CollectStatisticsDimensionFactory.getCollectStatisticsDimensionService(collectReportReqDto.getCollectType());
        //当前运单统计
        resData.setCollectReportDto(collectStatisticsService.queryCollectListPage(collectReportReqDto).get(0));
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
        List<CollectTypeEnum> collectTypeList = Arrays.asList(CollectTypeEnum.WAYBILL_BUQI, CollectTypeEnum.TASK_JIQI, CollectTypeEnum.SITE_JIQI);
        for(CollectTypeEnum en : collectTypeList) {
            //
            CollectStatisticsQueryParamDto queryParamDto = new CollectStatisticsQueryParamDto();
            queryParamDto.setBizId(collectReportReqDto.getBizId());
            queryParamDto.setCollectType(en.getCode());
            queryParamDto.setWaybillCode(collectReportReqDto.getWaybillCode());
            //
            CollectStatisticsDimensionService collectStatisticsService = CollectStatisticsDimensionFactory.getCollectStatisticsDimensionService(en.getCode());
            res.add(collectStatisticsService.collectStatistics(queryParamDto));
        }
        return res;
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
    public boolean initCollect(CollectDto collectDto, List<String> packageCodeList) {
        String methodDesc = "JyCollectServiceImpl.initCollect:集齐初始化:";
        CollectionCreatorEntity collectionCreatorEntity = getCollectionCreatorEntity(collectDto, packageCodeList);
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
    private CollectionCreatorEntity getCollectionCreatorEntity(CollectDto collectDto, List<String> packageCodeList) {
        CollectionCreatorEntity entity = new CollectionCreatorEntity();
        entity.setCollectionCodeEntity(getCollectionCodeEntity(collectDto));
        entity.setCollectionAggMarks(getNextSiteCodeMap(collectDto));
        entity.setCollectionScanCodeEntities(getCollectionScanCodeEntities(collectDto, packageCodeList));
        return entity;
    }
    //集齐初始化参数组装
    private List<CollectionScanCodeEntity> getCollectionScanCodeEntities(CollectDto collectDto, List<String> packageCodeList) {
        List<CollectionScanCodeEntity> resEntity = new ArrayList<>();

        Map<CollectionAggCodeTypeEnum, String> collectionAggCodeMaps = new HashMap<>();
        collectionAggCodeMaps.put(CollectionAggCodeTypeEnum.waybill_code, collectDto.getWaybillCode());
        for (String packageCode : packageCodeList) {
            CollectionScanCodeEntity entity = new CollectionScanCodeEntity();
            entity.setScanCode(packageCode);
            entity.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
            entity.setCollectedMark(collectDto.getBizId());
            entity.setCollectionAggCodeMaps(collectionAggCodeMaps);
            resEntity.add(entity);
        }
        return resEntity;
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
    @JProfiler(jKey = "JyCollectServiceImpl.removeCollect",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean removeCollect(CollectDto collectDto) {
        //todo  直接调用集齐服务初始化，集齐服务处理场地是末端还是中转
        return true;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.updateSingleCollectStatus",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult updateSingleCollectStatus(UnloadScanCollectDealDto unloadScanCollectDealDto) {
        return null;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.scanQueryCollectTypeStatistics",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public CollectReportStatisticsDto scanQueryCollectTypeStatistics(UnloadScanCollectDealDto unloadScanCollectDealDto) {
        return null;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.scanQueryWaybillCollectTypeStatistics",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public CollectReportStatisticsDto scanQueryWaybillCollectTypeStatistics(UnloadScanCollectDealDto unloadScanCollectDealDto) {
        return null;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.waybillBatchUpdateCollectStatus",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult waybillBatchUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto) {
        return null;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.collectWaitWaybillNum",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ScanCollectStatisticsDto> collectWaitWaybillNum(CollectStatisticsQueryDto reqDto) {
        InvokeResult<ScanCollectStatisticsDto> res = new InvokeResult<>();
        res.success();
        Integer collectType = CollectTypeEnum.WAYBILL_BUQI.getCode();
        CollectStatisticsQueryParamDto queryParamDto = new CollectStatisticsQueryParamDto();
        queryParamDto.setBizId(reqDto.getBizId());
        queryParamDto.setCollectType(collectType);
        //
        CollectStatisticsDimensionService collectStatisticsService = CollectStatisticsDimensionFactory.getCollectStatisticsDimensionService(collectType);
        CollectReportStatisticsDto collectReportStatisticsDto = collectStatisticsService.collectStatistics(queryParamDto);
        //
        ScanCollectStatisticsDto resData = new ScanCollectStatisticsDto();
        resData.setCollectType(collectType);
        resData.setWaybillBuQiNum(collectReportStatisticsDto.getStatisticsNum());
        res.setData(resData);
        return res;
    }



}
