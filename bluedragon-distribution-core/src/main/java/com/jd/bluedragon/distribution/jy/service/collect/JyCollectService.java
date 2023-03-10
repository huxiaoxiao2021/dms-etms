package com.jd.bluedragon.distribution.jy.service.collect;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.collect.*;
import com.jd.bluedragon.distribution.jy.dto.unload.CollectStatisticsQueryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanCollectStatisticsDto;

/**
 * @Author zhengchengfa
 * @Description ////集齐服务
 * @date
 **/
public interface JyCollectService {


    /**
     * 查询集齐统计信息
     * @param collectQueryReqDto
     * @return
     */
    InvokeResult<CollectReportResDto> findCollectInfo(CollectQueryReqDto collectQueryReqDto);

    /**
     * 查询集齐明细信息
     * @param collectQueryReqDto
     * @return
     */
    InvokeResult<CollectReportDetailResDto> findCollectDetail(CollectQueryReqDto collectQueryReqDto);

    /**
     * 解析该场地为运单的中转场地还是末端场地
     * @param scanCode
     * @param siteCode
     * @return CollectSiteTypeEnum
     */
    Integer parseSiteType(String scanCode, Integer siteCode);


    /**
     * 初始化保存集齐数据（封车、无任务扫描）
     * @param collectDto
     * @return
     */
    InvokeResult initCollect(CollectDto collectDto);
    /**
     * 消除集齐数据（取消封车）
     * @param collectDto
     * @return
     */
    InvokeResult removeCollect(CollectDto collectDto);

    /**
     * 修改单条集齐状态
     */
    InvokeResult updateSingleCollectStatus(UnloadScanCollectDealDto unloadScanCollectDealDto);

    /**
     * 扫描查询集齐统计数据
     */
    CollectReportStatisticsDto scanQueryCollectTypeStatistics(UnloadScanCollectDealDto unloadScanCollectDealDto);

    /**
     * 按单验货查询运单类型统计
     * 该接口直接运单的集齐类型，，按单验货一定齐了，但运单异步不一定初始化完，故只查类型，统计数据业务层自己处理
     * @return
     */
    CollectReportStatisticsDto scanQueryWaybillCollectTypeStatistics(UnloadScanCollectDealDto unloadScanCollectDealDto);

    /**
     * 按运单维度批量更新集齐状态
     * @param paramDto
     * @return
     */
    InvokeResult waybillBatchUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto);

    /**
     * 不齐运单统计数量查询
     * @param reqDto
     * @return
     */
    InvokeResult<ScanCollectStatisticsDto> collectWaitWaybillNum(CollectStatisticsQueryDto reqDto);
}
