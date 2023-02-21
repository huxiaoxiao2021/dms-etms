package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import com.jd.bluedragon.common.dto.select.SelectOption;

import java.util.List;

/**
 * 拣运评价网关服务
 */
public interface JyEvaluateGatewayService {

    /**
     * 评价维度枚举
     */
    JdCResponse<List<SelectOption>> dimensionOptions();

    /**
     * 查询评价与否
     */
    JdCResponse<Boolean> checkIsEvaluate(EvaluateTargetReq request);

    /**
     * 查询评价详情
     */
    JdCResponse<List<EvaluateDimensionDto>> findTargetEvaluateInfo(EvaluateTargetReq request);

    /**
     * 评价提交或修改
     */
    JdCResponse<Void> saveTargetEvaluate(EvaluateTargetReq request);


}
