package com.jd.bluedragon.distribution.jy.service.collect;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.collect.*;
import com.jd.bluedragon.distribution.jy.dto.unload.CollectStatisticsQueryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanCollectStatisticsDto;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectSiteTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectSiteTypeServiceFactory;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectStatisticsDimensionFactory;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectSiteTypeService;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectStatisticsDimensionService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //集齐服务实现
 * @date
 **/
public class JyCollectServiceImpl implements JyCollectService{

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
    @JProfiler(jKey = "JyCollectServiceImpl.initCollect",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult initCollect(CollectDto collectDto) {
        //todo zcf 直接调用集齐服务初始化，集齐服务处理场地是末端还是中转
//        CollectSiteTypeService collectSiteTypeService = getCollectSiteTypeService(collectDto);
//
//        collectSiteTypeService.initCollect(collectDto);

        return null;
    }

    @Override
    @JProfiler(jKey = "JyCollectServiceImpl.removeCollect",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult removeCollect(CollectDto collectDto) {
        //todo zcf 直接调用集齐服务初始化，集齐服务处理场地是末端还是中转
//
//        CollectSiteTypeService collectSiteTypeService = getCollectSiteTypeService(collectDto);
//
//        collectSiteTypeService.removeCollect(collectDto);

        return null;
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
