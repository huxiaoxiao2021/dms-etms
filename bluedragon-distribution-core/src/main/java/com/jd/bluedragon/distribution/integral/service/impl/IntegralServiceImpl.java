package com.jd.bluedragon.distribution.integral.service.impl;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.integral.domain.IntegralProxy;
import com.jd.bluedragon.distribution.integral.service.IntegralService;
import com.jd.tp.common.utils.Objects;
import com.jdl.jy.flat.entity.personalIntegralStatistics.JyIntegralDetailDTO;
import com.jdl.jy.flat.query.personalIntegralStatistics.JyIntegralDetailQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/19
 */
@Service
public class IntegralServiceImpl implements IntegralService {

    @Autowired
    IntegralProxy integralProxy;

    @Override
    public Response<JyIntegralDetailDTO> getSimpleJyIntegralInfo(JyIntegralDetailQuery var1) {
        Response<JyIntegralDetailDTO> response = new Response();
        if (Objects.isNull(var1.getUserCode()) || Objects.isNull(var1.getQueryDate())) {
            response.toError(Response.MESSAGE_WARN);
            return response;
        }
        try {
            response.setData(integralProxy.getSimpleJyIntegralInfo(var1));
            response.toSucceed();
            return response;
        } catch (Exception e) {
            response.toError(e.getMessage());
            return response;
        }
    }
}
