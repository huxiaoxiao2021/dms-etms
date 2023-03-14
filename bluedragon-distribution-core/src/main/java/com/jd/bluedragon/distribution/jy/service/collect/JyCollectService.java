package com.jd.bluedragon.distribution.jy.service.collect;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collection.entity.CollectionCodeEntity;
import com.jd.bluedragon.distribution.collection.entity.CollectionScanCodeEntity;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.collect.*;
import com.jd.bluedragon.distribution.jy.dto.unload.CollectStatisticsQueryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanCollectStatisticsDto;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Description ////集齐服务
 * @date
 **/
public interface JyCollectService {


    /**
     * 查询集齐统计信息
     * @param collectReportReqDto
     * @return
     */
    InvokeResult<CollectReportResDto> findCollectInfo(CollectReportReqDto collectReportReqDto);

    /**
     * 查询集齐明细信息
     * @param collectReportReqDto
     * @return
     */
    InvokeResult<CollectReportDetailResDto> findCollectDetail(CollectReportReqDto collectReportReqDto);

    /**
     * 解析该场地为运单的中转场地还是末端场地
     * @param scanCode
     * @param siteCode
     * @return CollectSiteTypeEnum
     */
    Integer parseSiteType(String scanCode, Integer siteCode);

    List<CollectionCodeEntity> getCollectionCodeEntityByElement (String bizCode, Integer siteCode, CollectionBusinessTypeEnum businessType);


    /**
     * 初始化保存集齐数据（封车、无任务扫描）
     * @param collectDto
     * @return
     */
    boolean initCollect(CollectDto collectDto, List<CollectionScanCodeEntity> detailList);

    /**
     * 集齐初始化 & 修改集齐状态
     * @param collectDto
     * @param detailList
     * @return
     */
    boolean initAndCollectedPartCollection(CollectDto collectDto, List<CollectionScanCodeEntity> detailList);
    /**
     * 消除集齐数据（取消发货、车型封车）
     * @param collectDto
     * @return
     */
    boolean removeCollect(CollectDto collectDto);

    /**
     * 修改单条集齐状态
     */
    boolean updateSingleCollectStatus(UnloadScanCollectDealDto unloadScanCollectDealDto);

    /**
     * 扫描查询集齐统计数据
     */
    CollectReportStatisticsDto scanQueryCollectTypeStatistics(UnloadScanCollectDealDto unloadScanCollectDealDto);

    /**
     * 按运单维度批量更新集齐状态
     * @param paramDto
     * @return
     */
    boolean waybillBatchUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto);

    /**
     * 不齐运单统计数量查询
     * @param reqDto
     * @return
     */
    InvokeResult<ScanCollectStatisticsDto> collectWaitWaybillNum(CollectStatisticsQueryDto reqDto);
    /**
     * 按条件查集齐报表
     * @param reqDto
     * @return
     */
    InvokeResult<CollectReportResDto> findCollectReportByScanCode(CollectReportQueryParamReqDto reqDto);
}
