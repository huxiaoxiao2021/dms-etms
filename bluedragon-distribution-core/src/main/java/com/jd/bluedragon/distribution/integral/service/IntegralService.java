package com.jd.bluedragon.distribution.integral.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.integral.response.JyIntegralDetailDTO;
import com.jd.bluedragon.common.dto.integral.response.JyIntegralDetailQuery;
import com.jd.bluedragon.common.dto.integral.response.JyIntroductionDTO;
import com.jd.bluedragon.common.dto.integral.response.JyRuleDescriptionDTO;

import java.util.List;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/19
 */
public interface IntegralService {

    /*
     * 查询个人积分总览
     */
    JdCResponse<JyIntegralDetailDTO> getSimpleJyIntegralInfo(JyIntegralDetailQuery query);

    /*
     * 查询基本分明细
     */
    JdCResponse<JyIntegralDetailDTO> getJyBaseScoreDetail(JyIntegralDetailQuery query);

    /*
     * 查询系数明细
     */
    JdCResponse<JyIntegralDetailDTO> getJyIntegralCoefficientDetail(JyIntegralDetailQuery query);

    /*
     * 获取规则介绍
     */
    JdCResponse<JyIntroductionDTO> getJyIntegralIntroduction();

    /*
     * 获取规则描述
     */
    JdCResponse<List<JyRuleDescriptionDTO>> queryQuotaDescriptionByCondition(JyIntegralDetailQuery query);
}