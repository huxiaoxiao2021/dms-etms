package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.integral.request.IntegralRequest;
import com.jd.bluedragon.common.dto.integral.response.JyIntegralDetailDTO;
import com.jd.bluedragon.common.dto.integral.response.JyIntegralDetailQuery;
import com.jd.bluedragon.common.dto.integral.response.JyIntroductionDTO;
import com.jd.bluedragon.common.dto.integral.response.JyRuleDescriptionDTO;
import com.jd.bluedragon.distribution.integral.service.IntegralService;
import com.jd.bluedragon.external.gateway.service.IntegralGatewayService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/23
 */
@Service("integralGatewayServiceImpl")
public class IntegralGatewayServiceImpl implements IntegralGatewayService {

    @Autowired
    IntegralService integralService;


    @Override
    public JdCResponse<JyIntegralDetailDTO> getSimpleJyIntegralInfo(IntegralRequest request) {
        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
        query.setUserCode(request.getUser().getUserErp());
        query.setSiteCode((long) request.getCurrentOperate().getSiteCode());
        query.setQueryDate(request.getQueryDate());
        return integralService.getSimpleJyIntegralInfo(query);
    }

    @Override
    public JdCResponse<JyIntegralDetailDTO> getJyBaseScoreDetail(IntegralRequest request) {
        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
        query.setUserCode(request.getUser().getUserErp());
        query.setQueryDate(request.getQueryDate());
        return integralService.getJyBaseScoreDetail(query);
    }

    @Override
    public JdCResponse<JyIntegralDetailDTO> getJyIntegralCoefficientDetail(IntegralRequest request) {
        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
        query.setUserCode(request.getUser().getUserErp());
        query.setQueryDate(request.getQueryDate());
        return integralService.getJyBaseScoreDetail(query);
    }

    @Override
    public JdCResponse<JyIntroductionDTO> getJyIntegralIntroduction(IntegralRequest request) {
        return integralService.getJyIntegralIntroduction();
    }

    @Override
    public JdCResponse<List<JyRuleDescriptionDTO>> queryQuotaDescriptionByCondition(IntegralRequest request) {
        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
        BeanUtils.copyProperties(request, query);
        return integralService.queryQuotaDescriptionByCondition(query);
    }
}
