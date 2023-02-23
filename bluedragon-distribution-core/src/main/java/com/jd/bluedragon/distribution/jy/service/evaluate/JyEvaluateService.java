package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import com.jd.bluedragon.common.dto.select.SelectOption;

import java.util.List;

public interface JyEvaluateService {

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
     * 评价提交
     */
    JdCResponse<Void> saveTargetEvaluate(EvaluateTargetReq request);

    /**
     * 评价修改
     */
    JdCResponse<Void> updateTargetEvaluate(EvaluateTargetReq request);

}
