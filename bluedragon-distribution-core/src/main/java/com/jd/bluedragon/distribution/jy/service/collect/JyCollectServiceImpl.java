package com.jd.bluedragon.distribution.jy.service.collect;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.collect.*;
import com.jd.bluedragon.distribution.jy.dto.unload.CollectStatisticsQueryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanCollectStatisticsDto;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectSiteTypeService;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectStatisticsDimensionService;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectSiteTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectSiteTypeServiceFactory;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectStatisticsDimensionFactory;

/**
 * @Author zhengchengfa
 * @Description //集齐服务实现
 * @date
 **/
public class JyCollectServiceImpl implements JyCollectService{

    @Override
    public InvokeResult<CollectReportResDto> findCollectInfo(CollectQueryReqDto collectQueryReqDto) {
        InvokeResult<CollectReportResDto> res = new InvokeResult<>();
        CollectReportResDto resData = new CollectReportResDto();
        res.setData(resData);

        CollectStatisticsDimensionService collectStatisticsService = CollectStatisticsDimensionFactory.getCollectStatisticsDimensionService(collectQueryReqDto.getCollectType());
        //集齐统计数据
        resData.setCollectReportStatisticsDtoList(collectStatisticsService.collectStatistics(collectQueryReqDto));
        //集齐运单列表
        resData.setCollectReportDtoList(collectStatisticsService.queryCollectListPage(collectQueryReqDto));

        return res;
    }

    @Override
    public InvokeResult<CollectReportDetailResDto> findCollectDetail(CollectQueryReqDto collectQueryReqDto) {
        InvokeResult<CollectReportDetailResDto> res = new InvokeResult<>();
        CollectReportDetailResDto resData = new CollectReportDetailResDto();
        res.setData(resData);

        CollectStatisticsDimensionService collectStatisticsService = CollectStatisticsDimensionFactory.getCollectStatisticsDimensionService(collectQueryReqDto.getCollectType());
        //集齐类型运单统计
        resData.setCollectReportStatisticsDtoList(collectStatisticsService.collectStatistics(collectQueryReqDto));
        //当前运单统计
        resData.setCollectReportDto(collectStatisticsService.queryCollectListPage(collectQueryReqDto).get(0));
        //
        resData.setCollectReportDetailPackageDtoList(collectStatisticsService.queryCollectDetail(collectQueryReqDto));
        return res;
    }

    @Override
    public Integer parseSiteType(String scanCode, Integer siteCode) {
        //是末级场地
        if(this.isEndSite(scanCode, siteCode)){
            return CollectSiteTypeEnum.WAYBILL.getCode();
        }
        //中转场地
        return CollectSiteTypeEnum.HANDOVER.getCode();
    }

    /**
     * 是否末端场地
     * @param scanCode
     * @param siteCode
     * @return
     */
    private boolean isEndSite(String scanCode, Integer siteCode) {
        //todo zcf  补充逻辑        运单末端场地redis缓存，K: 运单号 V: siteCode

        return  true;
    }

    @Override
    public InvokeResult initCollect(CollectDto collectDto) {
        CollectSiteTypeService collectSiteTypeService = getCollectSiteTypeService(collectDto);

        collectSiteTypeService.initCollect(collectDto);

        return null;
    }

    @Override
    public InvokeResult removeCollect(CollectDto collectDto) {
        CollectSiteTypeService collectSiteTypeService = getCollectSiteTypeService(collectDto);

        collectSiteTypeService.removeCollect(collectDto);

        return null;
    }

    @Override
    public InvokeResult updateSingleCollectStatus(UnloadScanCollectDealDto unloadScanCollectDealDto) {
        return null;
    }

    @Override
    public CollectReportStatisticsDto scanQueryCollectTypeStatistics(UnloadScanCollectDealDto unloadScanCollectDealDto) {
        return null;
    }

    @Override
    public CollectReportStatisticsDto scanQueryWaybillCollectTypeStatistics(UnloadScanCollectDealDto unloadScanCollectDealDto) {
        return null;
    }

    @Override
    public InvokeResult waybillBatchUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto) {
        return null;
    }

    @Override
    public InvokeResult<ScanCollectStatisticsDto> collectWaitWaybillNum(CollectStatisticsQueryDto reqDto) {
        return null;
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


}
