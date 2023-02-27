package com.jd.bluedragon.distribution.jy.evaluate.service;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoQuery;

import java.util.List;

/**
 * 装车评价jsf接口
 * 提供给分拣工作台
 */
public interface JyEvaluateJsfService {
    Result<List<JyEvaluateTargetInfoEntity>> queryPageList(JyEvaluateTargetInfoQuery query);

    Result<Long> queryCount(JyEvaluateTargetInfoQuery query);

    Result<JyEvaluateTargetInfoEntity> queryInfoByTargetBizId(String businessId);

    Result<List<JyEvaluateRecordEntity>> queryRecordByTargetBizId(String businessId);
}
