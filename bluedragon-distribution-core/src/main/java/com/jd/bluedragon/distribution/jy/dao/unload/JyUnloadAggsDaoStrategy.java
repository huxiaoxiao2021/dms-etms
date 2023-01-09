package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.distribution.jy.dto.unload.DimensionQueryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.GoodsCategoryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanStatisticsDto;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/12/23 15:10
 * @Description: 拣运卸车岗策略接口
 */
public interface JyUnloadAggsDaoStrategy {

    List<JyUnloadAggsEntity> queryByBizId(JyUnloadAggsEntity entity);

    //查包裹维度任务统计信息
    JyUnloadAggsEntity queryPackageStatistics(DimensionQueryDto dto);

    JyUnloadAggsEntity queryToScanAndMoreScanStatistics(String bizId);

    //查运单维度任务统计信息
    JyUnloadAggsEntity queryWaybillStatisticsUnderTask(DimensionQueryDto dto);

    JyUnloadAggsEntity queryWaybillStatisticsUnderBoard(DimensionQueryDto dto);

    List<GoodsCategoryDto> queryGoodsCategoryStatistics(JyUnloadAggsEntity entity);

    ScanStatisticsDto queryExcepScanStatistics(JyUnloadAggsEntity entity);

    JyUnloadAggsEntity queryBoardStatistics(DimensionQueryDto dto);


}
