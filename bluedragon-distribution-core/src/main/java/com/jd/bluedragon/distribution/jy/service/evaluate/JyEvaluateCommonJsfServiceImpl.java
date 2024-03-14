package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.jy.api.JyEvaluateCommonJsfService;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealAddDto;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealDto;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealRes;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author pengchong28
 * @description 装车评价通用对外jsf服务实现
 * @date 2024/3/1
 */
@Slf4j
@Service("jyEvaluateCommonJsfService")
public class JyEvaluateCommonJsfServiceImpl implements JyEvaluateCommonJsfService {

    @Autowired
    private JyEvaluateAppealService jyEvaluateAppealService;

    /**
     * 根据条件获取评价申诉列表
     * @param conditions 评价申诉条件列表
     * @return 评价申诉列表响应
     * @throws Exception 异常
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonJsfService.getListByCondition", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<List<JyEvaluateRecordAppealDto>> getListByCondition(
        List<String> conditions) {
        return jyEvaluateAppealService.getListByCondition(conditions);
    }

    /**
     * 批量添加装车评价申诉数据
     * @param addDto 待添加的装车评价申诉数实体列表
     * @return 响应是否成功的布尔值
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonJsfService.submitAppeal", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<Boolean> submitAppeal(JyEvaluateRecordAppealAddDto addDto) {
        return jyEvaluateAppealService.submitAppeal(addDto);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonJsfService.getDetailByCondition", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<List<JyEvaluateRecordAppealDto>> getDetailByCondition(JyEvaluateRecordAppealDto condition) {
        return jyEvaluateAppealService.getDetailByCondition(condition);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonJsfService.checkAppeal", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<Boolean> checkAppeal(JyEvaluateRecordAppealRes res) {
        return jyEvaluateAppealService.checkAppeal(res);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonJsfService.getAppealRejectCount", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<Integer> getAppealRejectCount(Long loadSiteCode) {
        return jyEvaluateAppealService.getAppealRejectCount(loadSiteCode);
    }
}
