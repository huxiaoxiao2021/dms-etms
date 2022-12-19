package com.jd.bluedragon.distribution.integral.domain;

import com.jd.bluedragon.distribution.workingConfig.WorkingConfigProxy;
import com.jd.fastjson.JSON;
import com.jd.tp.common.utils.Objects;
import com.jdl.jy.flat.api.PersonalIntegralStatistics.IJyPersonalIntegralStatisticsJSFService;
import com.jdl.jy.flat.base.ServiceResult;
import com.jdl.jy.flat.entity.personalIntegralStatistics.JyIntegralDetailDTO;
import com.jdl.jy.flat.query.personalIntegralStatistics.JyIntegralDetailQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/19
 */
@Service
public class IntegralProxy {

    private static final Logger log = LoggerFactory.getLogger(IntegralProxy.class);

    @Autowired
    IJyPersonalIntegralStatisticsJSFService personalIntegralJSFService;

    public JyIntegralDetailDTO getSimpleJyIntegralInfo(JyIntegralDetailQuery var1) {
        JyIntegralDetailDTO detailDTO = new JyIntegralDetailDTO();
        ServiceResult<JyIntegralDetailDTO> integralInfoResult = personalIntegralJSFService.getSimpleJyIntegralInfo(var1);
        if (integralInfoResult.getSuccess() && Objects.nonNull(integralInfoResult.getData())) {
            detailDTO = integralInfoResult.getData();
        } else {
            throw new RuntimeException("IntegralProxy.getSimpleJyIntegralInfo请求失败入参-" + JSON.toJSONString(var1));
        }
        return detailDTO;
    }

}
