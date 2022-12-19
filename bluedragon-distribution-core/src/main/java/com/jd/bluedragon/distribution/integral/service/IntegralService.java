package com.jd.bluedragon.distribution.integral.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jdl.jy.flat.entity.personalIntegralStatistics.JyIntegralDetailDTO;
import com.jdl.jy.flat.query.personalIntegralStatistics.JyIntegralDetailQuery;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/19
 */
public interface IntegralService {
    Response<JyIntegralDetailDTO> getSimpleJyIntegralInfo(JyIntegralDetailQuery var1);
}