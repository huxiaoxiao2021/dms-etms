package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.jy.dto.unload.ExcepScanDto;
import com.jd.bluedragon.distribution.jy.dto.unload.GoodsCategoryDto;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;

import java.util.List;

public interface JyUnloadAggsService {
    /**
     * aggs查询货物分类
     * @param entity
     * @return
     */
    List<GoodsCategoryDto> queryGoodsCategoryStatistics(JyUnloadAggsEntity entity);

    /**
     * 查询异常扫描统计信息
     * @param entity
     * @return
     */
    List<ExcepScanDto>  queryExcepScanStatistics(JyUnloadAggsEntity entity);
}
