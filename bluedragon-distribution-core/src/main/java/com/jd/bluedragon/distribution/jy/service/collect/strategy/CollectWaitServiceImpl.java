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
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectStatusEnum;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectStatisticsDimensionFactory;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author zhengchengfa
 * @Description //集齐等待服务实现（不齐）
 * @date
 **/
@Service("collectWaitServiceImpl")
public class CollectWaitServiceImpl implements CollectStatisticsDimensionService, InitializingBean {

    @Autowired
    private JyCollectService jyCollectService;

    @Autowired
    private CollectionRecordService collectionRecordService;


    @Override
    @JProfiler(jKey = "CollectWaitServiceImpl.queryCollectListPage",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<CollectReportDto> queryCollectListPage(CollectReportReqDto collectReportReqDto) {
        if (null == collectReportReqDto || null == collectReportReqDto.getCurrentOperate()) {
            return Collections.emptyList();
        }
        List<CollectionCodeEntity> collectionCodeEntities = jyCollectService.getCollectionCodeEntityByElement(
            collectReportReqDto.getBizId(), collectReportReqDto.getCurrentOperate().getSiteCode(), null
        );
        List<CollectionAggCodeCounter> collectionAggCodeCounters = collectionRecordService.sumCollectionByCollectionCodeAndStatus(
            collectionCodeEntities, CollectionStatusEnum.none_collected, CollectionAggCodeTypeEnum.waybill_code,
            collectReportReqDto.getWaybillCode(), collectReportReqDto.getBizId(),
            collectReportReqDto.getPageSize(), (collectReportReqDto.getPageNo() - 1) * collectReportReqDto.getPageSize());

        return collectionAggCodeCounters.parallelStream().map(collectionAggCodeCounter -> {
            CollectReportDto collectReportDto = new CollectReportDto();
//                collectReportDto.setGoodsAreaCode(collectionAggCodeCounter.getAggMark()); todo 查找货区编码
            collectReportDto.setNextSiteCode(Integer.valueOf(collectionAggCodeCounter.getAggMark()));
            collectReportDto.setScanDoNum(collectionAggCodeCounter.getInnerMarkCollectedNum());
            collectReportDto.setPackageNum(collectionAggCodeCounter.getSumScanNum());
            collectReportDto.setScanWaitNum(collectionAggCodeCounter.getInnerMarkNoneCollectedNum());
            collectReportDto.setWaybillCode(collectionAggCodeCounter.getAggCode());
            collectReportDto.setScanNullNum(collectionAggCodeCounter.getNoneMarkNoneCollectedNum());
            return collectReportDto;
        }).collect(Collectors.toList());

    }

    @Override
    @JProfiler(jKey = "CollectWaitServiceImpl.queryCollectDetail",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
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

        return collectionScanCodeDetails.parallelStream().map(
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
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CollectStatisticsDimensionFactory.registerCollectStatisticsDimension(CollectTypeEnum.WAYBILL_BUQI.getCode(), this);
    }
}
