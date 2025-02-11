package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
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
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Author zhengchengfa
 * @Description //本车任务集齐
 * @date
 **/
@Service("collectTaskServiceImpl")
public class CollectTaskServiceImpl implements CollectStatisticsDimensionService, InitializingBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JyCollectService jyCollectService;

    @Autowired
    private CollectionRecordService collectionRecordService;

    @Autowired
    private IJyUnloadVehicleManager jyUnloadVehicleManager;
    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    @JProfiler(jKey = "CollectTaskServiceImpl.queryCollectListPage",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<CollectReportDto> queryCollectListPage(CollectReportReqDto collectReportReqDto,ITSSetter tsSetter) {
        if (null == collectReportReqDto || null == collectReportReqDto.getCurrentOperate()) {
            return Collections.emptyList();
        }
        List<CollectionCodeEntity> collectionCodeEntities = jyCollectService.getCollectionCodeEntityByElement(
            collectReportReqDto.getBizId(), collectReportReqDto.getCurrentOperate().getSiteCode(), Boolean.TRUE.equals(collectReportReqDto.getManualCreateTaskFlag())
        );

        List<CollectionAggCodeCounter> collectionAggCodeCounters = collectionRecordService.sumCollectedAggCodeByCollectionCodeInnerMark(
            collectionCodeEntities.parallelStream().filter(
                collectionCodeEntity ->
                    StringUtils.isEmpty(collectReportReqDto.getCollectionCode())
                        || Objects.equals(collectionCodeEntity.getCollectionCode(), collectReportReqDto.getCollectionCode()))
                .collect(Collectors.toList()),
            CollectionAggCodeTypeEnum.waybill_code, collectReportReqDto.getWaybillCode(), collectReportReqDto.getBizId(),
            collectReportReqDto.getPageSize(), (collectReportReqDto.getPageNo() - 1) * collectReportReqDto.getPageSize());

        List<CollectReportDto> res = collectionAggCodeCounters.parallelStream().map(collectionAggCodeCounter -> {
            CollectReportDto collectReportDto = new CollectReportDto();
            collectReportDto.setWaybillCode(collectionAggCodeCounter.getAggCode());
            collectReportDto.setCollectionCode(collectionAggCodeCounter.getCollectionCode());
            if(NumberUtils.isCreatable(collectionAggCodeCounter.getAggMark())) {
                String goodsAreaCode = jyUnloadVehicleManager.getGoodsAreaCode(collectReportReqDto.getCurrentOperate().getSiteCode(), Integer.valueOf(collectionAggCodeCounter.getAggMark()));
                if(StringUtils.isEmpty(goodsAreaCode)) {
                    BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(Integer.valueOf(collectionAggCodeCounter.getAggMark()));
                    goodsAreaCode = (baseSite != null) ? ("未知货区-流向" + baseSite.getSiteName()) : "未知货区-未知流向";
                }
                 collectReportDto.setGoodsAreaCode(goodsAreaCode);
                collectReportDto.setNextSiteCode(Integer.valueOf(collectionAggCodeCounter.getAggMark()));
            }else {
                collectReportDto.setGoodsAreaCode("未知货区");
            }
            collectReportDto.setScanDoNum(collectionAggCodeCounter.getInnerMarkCollectedNum() + collectionAggCodeCounter.getInnerMarkExtraCollectedNum());
            collectReportDto.setPackageNum(collectionAggCodeCounter.getSumScanNum());
            collectReportDto.setInventoryFlag(
                    (!Objects.isNull(collectionAggCodeCounter.getBusinessType())
                            && CollectionBusinessTypeEnum.all_site_collection.equals(collectionAggCodeCounter.getBusinessType()))
                            ? true : false);
            return collectReportDto;
        }).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(res)) {
            if(log.isInfoEnabled()) {
                log.info("CollectTaskServiceImpl.queryCollectListPage 查询任务本车集齐运单列表，参数={}，未查到集齐运单列表数据", JsonUtils.toJSONString(collectReportReqDto));
            }
            return null;
        }
        if(log.isInfoEnabled()) {
            log.info("CollectTaskServiceImpl.queryCollectListPage 查询任务本车集齐运单列表，参数={}，返回列表数量为={}", JsonUtils.toJSONString(collectReportReqDto), res.size());
        }
        Timestamp timestamp = collectionRecordService.getMaxTimeStampByCollectionCodesAndCollectedMark
            (collectionCodeEntities, CollectionAggCodeTypeEnum.waybill_code, collectReportReqDto.getBizId());
        if(!Objects.isNull(timestamp)) {
            tsSetter.setTimeStamp(timestamp.getTime());
        }else {
            log.warn("CollectTaskServiceImpl.queryCollectListPage 查询任务本车集齐运单列表，参数={}返回最新时间戳为空，理论上一定有最新更新时间", JsonUtils.toJSONString(collectReportReqDto));
        }
        return res;
    }

    @Override
    @JProfiler(jKey = "CollectTaskServiceImpl.queryCollectDetail",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<CollectReportDetailPackageDto> queryCollectDetail(CollectReportReqDto collectReportReqDto,ITSSetter tsSetter) {
        if (null == collectReportReqDto || null == collectReportReqDto.getCurrentOperate()) {
            return Collections.emptyList();
        }
        List<CollectionCodeEntity> collectionCodeEntities = jyCollectService.getCollectionCodeEntityByElement(
            collectReportReqDto.getBizId(), collectReportReqDto.getCurrentOperate().getSiteCode(), Boolean.TRUE.equals(collectReportReqDto.getManualCreateTaskFlag())
        );

        List<CollectionScanCodeDetail> collectionScanCodeDetails = collectionRecordService.queryCollectionScanDetailByAggCode(
            collectionCodeEntities.parallelStream().filter(
                collectionCodeEntity ->
                    StringUtils.isEmpty(collectReportReqDto.getCollectionCode())
                        || Objects.equals(collectionCodeEntity.getCollectionCode(), collectReportReqDto.getCollectionCode()))
                .collect(Collectors.toList()),
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
                return packageDto;
            }).collect(Collectors.toList());

        if(log.isInfoEnabled()) {
            log.info("CollectTaskServiceImpl.queryCollectDetail 查询任务本车集齐运单明细列表，参数={}，返回列表数量为={}",
                    JsonUtils.toJSONString(collectReportReqDto), CollectionUtils.isEmpty(res) ? 0 : res.size());
        }
        Timestamp timestamp = collectionRecordService.getMaxTimeStampByCollectionCodesAndAggCode
            (collectionCodeEntities, CollectionAggCodeTypeEnum.waybill_code, collectReportReqDto.getWaybillCode());
        if (Objects.nonNull(timestamp)) {
            tsSetter.setTimeStamp(timestamp.getTime());
        }
        return res;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CollectStatisticsDimensionFactory.registerCollectStatisticsDimension(CollectTypeEnum.TASK_JIQI.getCode(), this);

    }
}
