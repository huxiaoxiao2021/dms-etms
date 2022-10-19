package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.jy.dto.unload.ExcepScanDto;
import com.jd.bluedragon.distribution.jy.dto.unload.GoodsCategoryDto;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;

import java.util.List;

public interface JyUnloadAggsService {

    int insert(JyUnloadAggsEntity entity);

    List<JyUnloadAggsEntity> queryByBizId(JyUnloadAggsEntity entity);

    List<GoodsCategoryDto> queryGoodsCategoryStatistics(JyUnloadAggsEntity entity);

    List<ExcepScanDto>  queryExcepScanStatistics(JyUnloadAggsEntity entity);
}
