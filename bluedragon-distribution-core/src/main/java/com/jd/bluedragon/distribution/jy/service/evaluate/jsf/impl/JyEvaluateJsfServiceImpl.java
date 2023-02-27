package com.jd.bluedragon.distribution.jy.service.evaluate.jsf.impl;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoQuery;
import com.jd.bluedragon.distribution.jy.evaluate.service.JyEvaluateJsfService;
import com.jd.bluedragon.distribution.jy.service.evaluate.JyEvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("jyEvaluateJsfService")
public class JyEvaluateJsfServiceImpl implements JyEvaluateJsfService {
    @Autowired
    private JyEvaluateService jyEvaluateService;
    @Override
    public Result<List<JyEvaluateTargetInfoEntity>> queryPageList(JyEvaluateTargetInfoQuery query) {
        return jyEvaluateService.queryPageList(query);
    }

    @Override
    public Result<Long> queryCount(JyEvaluateTargetInfoQuery query) {
        return jyEvaluateService.queryCount(query);
    }

    @Override
    public Result<JyEvaluateTargetInfoEntity> queryInfoByTargetBizId(String businessId) {
        return jyEvaluateService.queryInfoByTargetBizId(businessId);
    }

    @Override
    public Result<List<JyEvaluateRecordEntity>> queryRecordByTargetBizId(String businessId) {
        return jyEvaluateService.queryRecordByTargetBizId(businessId);
    }
}
