package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.jy.dto.unload.ExcepScanDto;
import com.jd.bluedragon.distribution.jy.dto.unload.GoodsCategoryDto;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;

import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/10/9 6:35 PM
 */
public interface JyUnloadAggsService {

    int insert(JyUnloadAggsEntity entity);

    List<JyUnloadAggsEntity> queryByBizId(JyUnloadAggsEntity entity);

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

    int insertOrUpdateJyUnloadCarAggsMain(JyUnloadAggsEntity entity);

    int insertOrUpdateJyUnloadCarAggsBak(JyUnloadAggsEntity entity);

    List<JyUnloadAggsEntity> getUnloadAggsMainData(JyUnloadAggsEntity query);

    List<JyUnloadAggsEntity> getUnloadAggsBakData(JyUnloadAggsEntity query);


}
