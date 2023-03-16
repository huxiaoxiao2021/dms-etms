package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.DimensionOption;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.evaluate.JyEvaluateService;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.external.gateway.service.JyEvaluateGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;



public class JyEvaluateGatewayServiceImpl implements JyEvaluateGatewayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JyEvaluateGatewayServiceImpl.class);

    private static final Integer REFRESH_CODE = 600;

    @Autowired
    private JyEvaluateService jyEvaluateService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateGatewayServiceImpl.dimensionOptions", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<DimensionOption>> dimensionOptions() {
        JdCResponse<List<DimensionOption>> result = new JdCResponse<>();
        result.toSucceed();
        try {
            List<DimensionOption> list = jyEvaluateService.dimensionOptions();
            result.setData(list);
        } catch (Exception e) {
            LOGGER.error("dimensionOptions|获取评价维度枚举列表接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateGatewayServiceImpl.checkIsEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> checkIsEvaluate(EvaluateTargetReq request) {
        JdCResponse<Boolean> result = new JdCResponse<>();
        result.toSucceed();
        try {
            String checkResult = checkCommonParameter(request.getUser(), request.getCurrentOperate());
            if (checkResult != null) {
                result.toFail(checkResult);
                return result;
            }
            if (StringUtils.isBlank(request.getSourceBizId())) {
                result.toFail("sourceBizId不能为空");
                return result;
            }
            Boolean flag = jyEvaluateService.checkIsEvaluate(request);
            result.setData(flag);
        } catch (Exception e) {
            LOGGER.error("checkIsEvaluate|查询目标评价与否接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateGatewayServiceImpl.findTargetEvaluateInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<EvaluateDimensionDto>> findTargetEvaluateInfo(EvaluateTargetReq request) {
        JdCResponse<List<EvaluateDimensionDto>> result = new JdCResponse<>();
        result.toSucceed();
        try {
            String checkResult = checkCommonParameter(request.getUser(), request.getCurrentOperate());
            if (checkResult != null) {
                result.toFail(checkResult);
                return result;
            }
            if (StringUtils.isBlank(request.getSourceBizId())) {
                result.toFail("sourceBizId不能为空");
                return result;
            }
            List<EvaluateDimensionDto> list = jyEvaluateService.findTargetEvaluateInfo(request);
            result.setData(list);
        } catch (Exception e) {
            LOGGER.error("findTargetEvaluateInfo|查询目标评价详情接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateGatewayServiceImpl.saveTargetEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> saveTargetEvaluate(EvaluateTargetReq request) {
        JdCResponse<Void> result = new JdCResponse<>();
        result.toSucceed();
        try {
            String checkResult = checkCommonParameter(request.getUser(), request.getCurrentOperate());
            if (checkResult != null) {
                result.toFail(checkResult);
                return result;
            }
            if (StringUtils.isBlank(request.getSourceBizId())) {
                result.toFail("sourceBizId不能为空");
                return result;
            }
            if (request.getStatus() == null) {
                result.toFail("status不能为空");
                return result;
            }
            if (Constants.NUMBER_ZERO.equals(request.getStatus())) {
                if (CollectionUtils.isEmpty(request.getDimensionList())) {
                    result.toFail("评价维度列表详情不能为空");
                    return result;
                }
            }
            jyEvaluateService.saveTargetEvaluate(request);
        } catch (JyBizException e) {
            LOGGER.error("saveTargetEvaluate|创建评价目标基础信息出错,msg={}", e.getMessage());
            result.toError(e.getMessage());
            return result;
        } catch (LoadIllegalException e) {
            LOGGER.error("saveTargetEvaluate|创建评价目标基础信息出错,msg={}", e.getMessage());
            result.setCode(REFRESH_CODE);
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            LOGGER.error("saveTargetEvaluate|保存评价详情接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateGatewayServiceImpl.updateTargetEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> updateTargetEvaluate(EvaluateTargetReq request) {
        JdCResponse<Void> result = new JdCResponse<>();
        result.toSucceed();
        try {
            String checkResult = checkCommonParameter(request.getUser(), request.getCurrentOperate());
            if (checkResult != null) {
                result.toFail(checkResult);
                return result;
            }
            if (StringUtils.isBlank(request.getSourceBizId())) {
                result.toFail("sourceBizId不能为空");
                return result;
            }
            if (request.getStatus() == null) {
                result.toFail("status不能为空");
                return result;
            }
            if (Constants.NUMBER_ONE.equals(request.getStatus())) {
                result.toFail("无法从不满意修改为满意");
                return result;
            }
            if (CollectionUtils.isEmpty(request.getDimensionList())) {
                result.toFail("评价维度列表详情不能为空");
                return result;
            }
            jyEvaluateService.updateTargetEvaluate(request);
        } catch (JyBizException e) {
            LOGGER.error("updateTargetEvaluate|修改评价目标基础信息出错,msg={}", e.getMessage());
            result.toError(e.getMessage());
            return result;
        } catch (LoadIllegalException e) {
            LOGGER.error("updateTargetEvaluate|修改评价目标基础信息出错,msg={}", e.getMessage());
            result.setCode(REFRESH_CODE);
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            LOGGER.error("updateTargetEvaluate|修改评价详情接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
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
