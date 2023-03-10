package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.distribution.jy.dto.collect.*;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //集齐数据统计类型扩展
 * @date
 **/
public interface CollectStatisticsDimensionService {

    /**
     * 查询集齐统计
     */
    CollectReportStatisticsDto collectStatistics(CollectStatisticsQueryParamDto paramDto);

    /**
     * 查询集齐列表（分页）
     */
    List<CollectReportDto> queryCollectListPage(CollectReportReqDto collectReportReqDto);

    /**
     * 查询集齐明细
     */
    List<CollectReportDetailPackageDto> queryCollectDetail(CollectReportReqDto collectReportReqDto);
}
