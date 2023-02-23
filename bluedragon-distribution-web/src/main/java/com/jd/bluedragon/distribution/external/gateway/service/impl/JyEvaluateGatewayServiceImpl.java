package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.jy.service.evaluate.JyEvaluateService;
import com.jd.bluedragon.external.gateway.service.JyEvaluateGatewayService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;



public class JyEvaluateGatewayServiceImpl implements JyEvaluateGatewayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JyEvaluateGatewayServiceImpl.class);

    @Autowired
    private JyEvaluateService jyEvaluateService;

    @Override
    public JdCResponse<List<SelectOption>> dimensionOptions() {
        return jyEvaluateService.dimensionOptions();
    }

    @Override
    public JdCResponse<Boolean> checkIsEvaluate(EvaluateTargetReq request) {
        String checkResult = checkCommonParameter(request.getUser(), request.getCurrentOperate());
        if (checkResult != null) {
            JdCResponse<Boolean> result = new JdCResponse<>();
            result.toFail(checkResult);
            return result;
        }
        if (StringUtils.isBlank(request.getSourceBizId())) {
            JdCResponse<Boolean> result = new JdCResponse<>();
            result.toFail("sourceBizId不能为空");
            return result;
        }
        return jyEvaluateService.checkIsEvaluate(request);
    }

    @Override
    public JdCResponse<List<EvaluateDimensionDto>> findTargetEvaluateInfo(EvaluateTargetReq request) {
        return null;
    }

    @Override
    public JdCResponse<Void> saveTargetEvaluate(EvaluateTargetReq request) {
        return null;
    }

    @Override
    public JdCResponse<Void> updateTargetEvaluate(EvaluateTargetReq request) {
        return null;
    }

    private String checkCommonParameter(User user, CurrentOperate currentOperate) {
        if (user.getUserCode() <= 0) {
            return "userCode不合法";
        }
        if (StringUtils.isBlank(user.getUserName())) {
            return "userName不能为空";
        }
        if (StringUtils.isBlank(user.getUserErp())) {
            return "userErp不能为空";
        }
        if (currentOperate.getSiteCode() <= 0) {
            return "siteCode不合法";
        }
        if (StringUtils.isBlank(currentOperate.getSiteName())) {
            return "siteName不能为空";
        }
        if (StringUtils.isBlank(currentOperate.getDmsCode())) {
            return "dmsCode不能为空";
        }
        if (currentOperate.getOrgId() == null) {
            return "orgId不合法";
        }
        if (StringUtils.isBlank(currentOperate.getOrgName())) {
            return "orgName不能为空";
        }
        return null;
    }

}
