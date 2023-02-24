package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

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

    /**
     * 评价基础信息分页查询
     */
    Result<PageDto<JyEvaluateTargetInfoEntity>> queryPageList(JyEvaluateTargetInfoQuery query);

    /**
     * 根据条件查询数量
     * @param query
     * @return
     */
    Result<Long> queryCount(JyEvaluateTargetInfoQuery query);

    /**
     * 根据被评价目标业务主键查询评价记录
     * @param businessId
     * @return
     */
    Result<List<JyEvaluateRecordEntity>> queryRecordByTargetBizId(String businessId);

    /**
     * 根据被评价目标业务主键查询评价基础信息
     * @param businessId
     * @return
     */
    Result<JyEvaluateTargetInfoEntity> queryInfoByTargetBizId(String businessId);
}
