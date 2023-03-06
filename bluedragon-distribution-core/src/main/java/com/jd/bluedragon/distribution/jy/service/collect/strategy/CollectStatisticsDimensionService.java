package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.distribution.jy.dto.collect.CollectQueryReqDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportDetailPackageDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportStatisticsDto;

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
    abstract List<CollectReportStatisticsDto> collectStatistics(CollectQueryReqDto collectQueryReqDto);

    /**
     * 查询集齐列表（分页）
     */
    abstract List<CollectReportDto> queryCollectListPage(CollectQueryReqDto collectQueryReqDto);

    /**
     * 查询集齐明细
     */
    abstract List<CollectReportDetailPackageDto> queryCollectDetail(CollectQueryReqDto collectQueryReqDto);
}
