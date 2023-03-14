package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.distribution.collection.entity.CollectionAggCodeCounter;
import com.jd.bluedragon.distribution.collection.entity.CollectionCodeEntity;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionStatusEnum;
import com.jd.bluedragon.distribution.collection.service.CollectionRecordService;
import com.jd.bluedragon.distribution.jy.dto.collect.*;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectStatisticsDimensionFactory;
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
    public List<CollectReportDetailPackageDto> queryCollectDetail(CollectReportReqDto collectReportReqDto) {
        //todo zcf
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CollectStatisticsDimensionFactory.registerCollectStatisticsDimension(CollectTypeEnum.WAYBILL_BUQI.getCode(), this);
    }
}
