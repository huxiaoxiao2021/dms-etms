package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.DimensionOption;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;

import java.util.List;

public interface JyEvaluateService {

    /**
     * 评价维度枚举
     */
    List<DimensionOption> dimensionOptions();

    /**
     * 查询评价与否
     */
    Boolean checkIsEvaluate(EvaluateTargetReq request);

    /**
     * 查询评价详情
     */
    List<EvaluateDimensionDto> findTargetEvaluateInfo(EvaluateTargetReq request);

    /**
     * 评价提交
     */
    void saveTargetEvaluate(EvaluateTargetReq request);

    /**
     * 评价修改
     */
    void updateTargetEvaluate(EvaluateTargetReq request);
}
