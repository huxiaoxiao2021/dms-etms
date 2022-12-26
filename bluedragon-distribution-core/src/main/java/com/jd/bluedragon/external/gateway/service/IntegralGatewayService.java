package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.integral.request.IntegralRequest;
import com.jd.bluedragon.common.dto.integral.response.JyIntegralDetailDTO;
import com.jd.bluedragon.common.dto.integral.response.JyIntroductionDTO;
import com.jd.bluedragon.common.dto.integral.response.JyRuleDescriptionDTO;

import java.util.List;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/23
 */
public interface IntegralGatewayService {
    /*
     * 查询个人积分总览
     */
    JdCResponse<JyIntegralDetailDTO> getSimpleJyIntegralInfo(IntegralRequest request);

    /*
     * 查询基本分明细
     */
    JdCResponse<JyIntegralDetailDTO> getJyBaseScoreDetail(IntegralRequest request);

    /*
     * 查询系数明细
     */
    JdCResponse<JyIntegralDetailDTO> getJyIntegralCoefficientDetail(IntegralRequest request);

    /*
     * 获取规则介绍
     */
    JdCResponse<JyIntroductionDTO> getJyIntegralIntroduction();

    /*
     * 获取规则描述
     */
    JdCResponse<List<JyRuleDescriptionDTO>> queryQuotaDescriptionByCondition(IntegralRequest query);
}
