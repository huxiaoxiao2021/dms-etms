package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.collection.entity.CollectionAggCodeCounter;
import com.jd.bluedragon.distribution.collection.entity.CollectionCodeEntity;
import com.jd.bluedragon.distribution.collection.entity.CollectionScanCodeDetail;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionCollectedMarkTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionStatusEnum;
import com.jd.bluedragon.distribution.collection.service.CollectionRecordService;
import com.jd.bluedragon.distribution.jy.dto.collect.*;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectStatusEnum;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectStatisticsDimensionFactory;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author zhengchengfa
 * @Description //场地集齐服务
 * @date
 **/
@Service("collectSiteServiceImpl")
public class CollectSiteServiceImpl implements CollectStatisticsDimensionService, InitializingBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JyCollectService jyCollectService;

    @Autowired
    private CollectionRecordService collectionRecordService;

    @Autowired
    private IJyUnloadVehicleManager jyUnloadVehicleManager;


    @Override
    @JProfiler(jKey = "CollectSiteServiceImpl.queryCollectListPage",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<CollectReportDto> queryCollectListPage(CollectReportReqDto collectReportReqDto,ITSSetter tsSetter) {
        if (null == collectReportReqDto || null == collectReportReqDto.getCurrentOperate()) {
            return Collections.emptyList();
        }
        List<CollectionCodeEntity> collectionCodeEntities = jyCollectService.getCollectionCodeEntityByElement(
            collectReportReqDto.getBizId(), collectReportReqDto.getCurrentOperate().getSiteCode(), CollectionBusinessTypeEnum.all_site_collection
        );
        List<CollectionAggCodeCounter> collectionAggCodeCounters = collectionRecordService.sumCollectionByCollectionCodeAndStatus(
            collectionCodeEntities, CollectionStatusEnum.collected, CollectionAggCodeTypeEnum.waybill_code,
            collectReportReqDto.getWaybillCode(), collectReportReqDto.getBizId(),
            collectReportReqDto.getPageSize(), (collectReportReqDto.getPageNo() - 1) * collectReportReqDto.getPageSize());

        List<CollectReportDto> res = collectionAggCodeCounters.parallelStream().map(collectionAggCodeCounter -> {
            CollectReportDto collectReportDto = new CollectReportDto();
            if(collectionAggCodeCounter.getAggMark() != null) {
                String goodsAreaCode = jyUnloadVehicleManager.getGoodsAreaCode(collectReportReqDto.getCurrentOperate().getSiteCode(), Integer.valueOf(collectionAggCodeCounter.getAggMark()));
                collectReportDto.setGoodsAreaCode(goodsAreaCode);
                collectReportDto.setNextSiteCode(Integer.valueOf(collectionAggCodeCounter.getAggMark()));
            }
            collectReportDto.setScanDoNum(collectionAggCodeCounter.getInnerMarkCollectedNum());
            collectReportDto.setPackageNum(collectionAggCodeCounter.getSumScanNum());
            collectReportDto.setOtherInventoryNum(collectionAggCodeCounter.getOutMarkCollectedNum());

            return collectReportDto;
        }).collect(Collectors.toList());
        if(log.isInfoEnabled()) {
            log.info("CollectSiteServiceImpl.queryCollectListPage 查询在库集齐运单列表，参数={}，返回列表数量为={}",
                    JsonUtils.toJSONString(collectReportReqDto), CollectionUtils.isEmpty(res) ? 0 : res.size());
        }
        tsSetter.setTimeStamp(collectionAggCodeCounters.parallelStream().map(CollectionAggCodeCounter::getTs).max(
            Timestamp::compareTo).orElse(new Timestamp(0)).getTime());
        return res;
    }

    @Override
    @JProfiler(jKey = "CollectSiteServiceImpl.queryCollectDetail",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<CollectReportDetailPackageDto> queryCollectDetail(CollectReportReqDto collectReportReqDto) {
        if (null == collectReportReqDto || null == collectReportReqDto.getCurrentOperate()) {
            return Collections.emptyList();
        }
        List<CollectionCodeEntity> collectionCodeEntities = jyCollectService.getCollectionCodeEntityByElement(
            collectReportReqDto.getBizId(), collectReportReqDto.getCurrentOperate().getSiteCode(), CollectionBusinessTypeEnum.all_site_collection
        );

        List<CollectionScanCodeDetail> collectionScanCodeDetails = collectionRecordService.queryCollectionScanDetailByAggCode(collectionCodeEntities,
            collectReportReqDto.getWaybillCode(), CollectionAggCodeTypeEnum.waybill_code, collectReportReqDto.getBizId(),
            collectReportReqDto.getPageSize(), (collectReportReqDto.getPageNo() - 1) * collectReportReqDto.getPageSize());

        List<CollectReportDetailPackageDto> res = collectionScanCodeDetails.parallelStream().map(
            (Function<CollectionScanCodeDetail, CollectReportDetailPackageDto>)collectionScanCodeDetail -> {
                CollectReportDetailPackageDto packageDto = new CollectReportDetailPackageDto();
                packageDto.setPackageCode(collectionScanCodeDetail.getScanCode());

                if (CollectionCollectedMarkTypeEnum.none.equals(collectionScanCodeDetail.getCollectedMarkType())) {
                    packageDto.setPackageCollectStatus(CollectStatusEnum.SCAN_NULL.getCode());//未到
                } else if (CollectionStatusEnum.none_collected.equals(collectionScanCodeDetail.getCollectedStatus())
                    && CollectionCollectedMarkTypeEnum.inner.equals(collectionScanCodeDetail.getCollectedMarkType())) {
                    packageDto.setPackageCollectStatus(CollectStatusEnum.SCAN_WAIT.getCode());//未扫
                } else if (CollectionStatusEnum.collected.equals(collectionScanCodeDetail.getCollectedStatus())
                    && CollectionCollectedMarkTypeEnum.inner.equals(collectionScanCodeDetail.getCollectedMarkType())) {
                    packageDto.setPackageCollectStatus(CollectStatusEnum.SCAN_DO.getCode());//已扫
                } else if (CollectionStatusEnum.collected.equals(collectionScanCodeDetail.getCollectedStatus())
                    && CollectionCollectedMarkTypeEnum.outer.equals(collectionScanCodeDetail.getCollectedMarkType())) {
                    packageDto.setPackageCollectStatus(CollectStatusEnum.SCAN_END.getCode());//在库
                } else if (CollectionStatusEnum.extra_collected.equals(collectionScanCodeDetail.getCollectedStatus())) {
                    packageDto.setPackageCollectStatus(CollectStatusEnum.SCAN_DO.getCode());
                }
                return null;
            }).collect(Collectors.toList());
        if(log.isInfoEnabled()) {
            log.info("CollectSiteServiceImpl.queryCollectDetail 查询在库集齐运单明细列表，参数={}，返回列表数量为={}",
                    JsonUtils.toJSONString(collectReportReqDto), CollectionUtils.isEmpty(res) ? 0 : res.size());
        }
        return res;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CollectStatisticsDimensionFactory.registerCollectStatisticsDimension(CollectTypeEnum.SITE_JIQI.getCode(), this);
    }
}
